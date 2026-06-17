package com.travel.app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.app.dtos.PersonDTO;
import com.travel.app.dtos.ReservationRequestDTO;
import com.travel.app.dtos.TourPackageDTO;
import com.travel.app.entities.ReservationEntity;
import com.travel.app.entities.ReservationPassengerEntity;
import com.travel.app.enums.ReservationStatus;
import com.travel.app.repositories.ReservationPassengerRepository;
import com.travel.app.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationPassengerRepository reservationPassengerRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<ReservationEntity> getReservations() {
        List<ReservationEntity> reservations = reservationRepository.findAll();
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public ReservationEntity getReservationById(Long id) {
        ReservationEntity reservation = reservationRepository.findById(id).orElse(null);
        if (reservation != null && reservation.getActive() == 1) {
            return enrichReservation(reservation);
        }
        return null;
    }

    @Transactional
    public ReservationEntity changeStatus(Long id, String status, Long userId) {
        ReservationEntity reservation = getReservationById(id);
        if (reservation == null) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }

        try {
            ReservationStatus newStatus = ReservationStatus.valueOf(status);
            ReservationStatus oldStatus = reservation.getStatus();

            if (!isValidStatusTransition(oldStatus, newStatus)) {
                throw new RuntimeException(
                        String.format("No se puede cambiar de %s a %s", oldStatus, newStatus)
                );
            }

            reservation.setStatus(newStatus);
            reservation.setModifiedByUserId(userId);
            reservation.setUpdatedAt(LocalDateTime.now());

            return enrichReservation(reservationRepository.save(reservation));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado invalido: " + status);
        }
    }

    private boolean isValidStatusTransition(ReservationStatus oldStatus, ReservationStatus newStatus) {
        return switch (oldStatus) {
            case PENDIENTE -> newStatus == ReservationStatus.PAGADA ||
                    newStatus == ReservationStatus.CANCELADA ||
                    newStatus == ReservationStatus.EXPIRADA;
            case PAGADA -> newStatus == ReservationStatus.CANCELADA;
            case CANCELADA, EXPIRADA -> false;
        };
    }

    @Transactional
    public ReservationEntity createReservation(ReservationRequestDTO request, Long userId) {
        TourPackageDTO tourPackage = getTourPackageById(request.getTourPackageId());
        if (tourPackage == null) {
            throw new RuntimeException("Paquete turistico no encontrado");
        }

        int passengers = request.getPassengers() != null ? request.getPassengers() : 1;
        int availableSlots = getAvailableSlotsForPackage(tourPackage);
        if (availableSlots < passengers) {
            throw new RuntimeException(
                    String.format("No hay suficientes cupos disponibles. Cupos disponibles: %d, Solicitados: %d",
                            availableSlots, passengers)
            );
        }

        PersonDTO mainPerson = resolveMainPerson(request, userId);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setPersonId(mainPerson.getId());
        reservation.setTourPackageId(tourPackage.getId());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setExpirationDate(LocalDateTime.now().plusDays(3));
        reservation.setStatus(ReservationStatus.PENDIENTE);
        reservation.setActive(1);
        reservation.setCreatedByUserId(userId);
        reservation.setModifiedByUserId(userId);
        reservation.setSolicitudes(request.getSpecialRequests());
        reservation.setPassengersCount(passengers);
        reservation.setSubtotal(request.getSubtotal());
        reservation.setTotalAmount(request.getTotalAmount());
        reservation.setDiscountAmount(request.getDiscountAmount());

        if (request.getDiscountsDetail() != null && !request.getDiscountsDetail().isEmpty()) {
            try {
                reservation.setDiscountDetails(new ObjectMapper().writeValueAsString(request.getDiscountsDetail()));
            } catch (Exception e) {
                System.err.println("Error guardando detalles de descuentos: " + e.getMessage());
            }
        }

        ReservationEntity savedReservation = reservationRepository.save(reservation);

        if (request.getPassengersData() != null) {
            for (ReservationRequestDTO.PassengerDataDTO passengerData : request.getPassengersData()) {
                PersonDTO person = resolvePassenger(passengerData, userId);

                ReservationPassengerEntity reservationPassenger = new ReservationPassengerEntity();
                reservationPassenger.setReservation(savedReservation);
                reservationPassenger.setPersonId(person.getId());
                reservationPassenger.setActive(1);
                reservationPassenger.setCreatedByUserId(userId);
                reservationPassenger.setModifiedByUserId(userId);
                reservationPassengerRepository.save(reservationPassenger);
            }
        }

        return enrichReservation(savedReservation);
    }

    private PersonDTO resolveMainPerson(ReservationRequestDTO request, Long userId) {
        if (request.getPersonId() != null) {
            PersonDTO person = getPersonById(request.getPersonId());
            if (person == null) {
                throw new RuntimeException("Persona no encontrada con ID: " + request.getPersonId());
            }
            return person;
        }

        if (request.getIdentification() == null || request.getIdentification().isBlank()) {
            throw new RuntimeException("Se requiere la identificacion de la persona principal");
        }

        PersonDTO person = findPerson(request.getIdentification());
        if (person != null) {
            return person;
        }

        PersonDTO newPerson = new PersonDTO();
        newPerson.setFullName(request.getFullName());
        newPerson.setIdentification(request.getIdentification());
        newPerson.setEmail(request.getEmail());
        newPerson.setPhone(request.getPhone() != null ? request.getPhone() : "");
        newPerson.setNationality(request.getNationality() != null ? request.getNationality() : "");
        newPerson.setActive(1);
        newPerson.setCreatedByUserId(userId);
        newPerson.setUpdatedByUserId(userId);
        return savePerson(newPerson);
    }

    private PersonDTO resolvePassenger(ReservationRequestDTO.PassengerDataDTO passengerData, Long userId) {
        if (passengerData.getPersonId() != null) {
            PersonDTO person = getPersonById(passengerData.getPersonId());
            if (person == null) {
                throw new RuntimeException("Persona no encontrada con ID: " + passengerData.getPersonId());
            }
            return person;
        }

        PersonDTO person = findPerson(passengerData.getIdentification());
        if (person != null) {
            return person;
        }

        PersonDTO newPerson = new PersonDTO();
        newPerson.setFullName(passengerData.getFullName());
        newPerson.setIdentification(passengerData.getIdentification());
        newPerson.setEmail(passengerData.getEmail());
        newPerson.setPhone(passengerData.getPhone() != null ? passengerData.getPhone() : "");
        newPerson.setNationality(passengerData.getNationality() != null ? passengerData.getNationality() : "");
        newPerson.setActive(1);
        newPerson.setCreatedByUserId(userId);
        newPerson.setUpdatedByUserId(userId);
        return savePerson(newPerson);
    }

    private int getAvailableSlotsForPackage(TourPackageDTO tourPackage) {
        if (tourPackage.getTotalSlots() == null) {
            return 0;
        }

        Integer reservedPassengers = reservationRepository.countConfirmedPassengersByPackageId(tourPackage.getId());
        if (reservedPassengers == null) {
            reservedPassengers = 0;
        }

        return tourPackage.getTotalSlots() - reservedPassengers;
    }

    @Transactional
    public ReservationEntity updateReservation(ReservationEntity reservation) {
        ReservationEntity existingReservation = reservationRepository.findById(reservation.getId()).orElse(null);
        if (existingReservation == null) {
            throw new RuntimeException("Reserva no encontrada");
        }

        if (reservation.getStatus() != null) {
            existingReservation.setStatus(reservation.getStatus());
        }
        if (reservation.getActive() != null) {
            existingReservation.setActive(reservation.getActive());
        }
        existingReservation.setModifiedByUserId(reservation.getModifiedByUserId());
        existingReservation.setUpdatedAt(LocalDateTime.now());

        return enrichReservation(reservationRepository.save(existingReservation));
    }

    public List<ReservationEntity> getByPersonId(Long personId) {
        List<ReservationEntity> reservations = reservationRepository.findByPersonIdAndActive(personId, 1);
        reservations.forEach(this::enrichReservation);
        return reservations;
    }

    public List<ReservationPassengerEntity> getPassengersByReservationId(Long reservationId) {
        List<ReservationPassengerEntity> passengers =
                reservationPassengerRepository.findByReservationIdAndActive(reservationId, 1);
        passengers.forEach(this::enrichPassenger);
        return passengers;
    }

    public int countConfirmedPassengersByPackageId(Long packageId) {
        return reservationRepository.countConfirmedPassengersByPackageId(packageId);
    }

    private ReservationEntity enrichReservation(ReservationEntity reservation) {
        if (reservation == null) {
            return null;
        }
        reservation.setPerson(getPersonById(reservation.getPersonId()));
        reservation.setTourPackage(getTourPackageById(reservation.getTourPackageId()));
        return reservation;
    }

    private ReservationPassengerEntity enrichPassenger(ReservationPassengerEntity passenger) {
        if (passenger != null) {
            passenger.setPerson(getPersonById(passenger.getPersonId()));
        }
        return passenger;
    }

    private PersonDTO getPersonById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return restTemplate.getForObject("http://m1-person-service/api/persons/" + id, PersonDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private PersonDTO findPerson(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            return restTemplate.getForObject("http://m1-person-service/api/persons/search?query=" + encoded,
                    PersonDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private PersonDTO savePerson(PersonDTO person) {
        try {
            return restTemplate.postForObject("http://m1-person-service/api/persons/", person, PersonDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la persona en M1: " + e.getMessage());
        }
    }

    private TourPackageDTO getTourPackageById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return restTemplate.getForObject("http://m2-package-service/api/tour-packages/" + id, TourPackageDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
}

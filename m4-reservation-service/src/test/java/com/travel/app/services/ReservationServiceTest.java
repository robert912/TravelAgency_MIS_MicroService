package com.travel.app.services;

import com.travel.app.dtos.ReservationRequestDTO;
import com.travel.app.entities.PersonEntity;
import com.travel.app.entities.ReservationEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.enums.ReservationStatus;
import com.travel.app.repositories.ReservationPassengerRepository;
import com.travel.app.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationPassengerRepository reservationPassengerRepository;

    @Mock
    private PersonService personService;

    @Mock
    private TourPackageService tourPackageService;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationEntity reservation;
    private TourPackageEntity tourPackage;
    private PersonEntity person;

    @BeforeEach
    void setUp() {
        person = new PersonEntity();
        person.setId(1L);
        person.setIdentification("123456");

        tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);
        tourPackage.setTotalSlots(10);

        reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PENDIENTE);
        reservation.setActive(1);
        reservation.setPerson(person);
        reservation.setTourPackage(tourPackage);
    }

    @Test
    void getReservations_ShouldReturnList() {
        when(reservationRepository.findAll()).thenReturn(Arrays.asList(reservation));

        List<ReservationEntity> result = reservationService.getReservations();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void getReservationById_WhenExistsAndActive_ShouldReturnReservation() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationEntity result = reservationService.getReservationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void changeStatus_WhenValidTransition_ShouldUpdateStatus() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity result = reservationService.changeStatus(1L, "PAGADA", 2L);

        assertNotNull(result);
        assertEquals(ReservationStatus.PAGADA, reservation.getStatus());
        assertEquals(2L, reservation.getModifiedByUserId());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void changeStatus_WhenInvalidTransition_ShouldThrowException() {
        reservation.setStatus(ReservationStatus.CANCELADA);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.changeStatus(1L, "PAGADA", 2L);
        });

        assertTrue(exception.getMessage().contains("No se puede cambiar"));
    }

    @Test
    void createReservation_WhenEnoughSlots_ShouldCreate() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setTourPackageId(1L);
        request.setPassengers(2);
        request.setPersonId(1L);
        request.setPassengersData(Collections.emptyList());

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(5);
        when(personService.getPersonById(1L)).thenReturn(person);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity result = reservationService.createReservation(request, 2L);

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void createReservation_WhenNotEnoughSlots_ShouldThrowException() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setTourPackageId(1L);
        request.setPassengers(6); // 10 total - 5 confirmed = 5 available. 6 is too much.

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(5);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.createReservation(request, 2L);
        });

        assertTrue(exception.getMessage().contains("No hay suficientes cupos"));
    }

    @Test
    void updateReservation_ShouldUpdateFields() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity updateData = new ReservationEntity();
        updateData.setId(1L);
        updateData.setStatus(ReservationStatus.PAGADA);
        updateData.setActive(0);
        updateData.setModifiedByUserId(3L);

        ReservationEntity result = reservationService.updateReservation(updateData);

        assertEquals(ReservationStatus.PAGADA, result.getStatus());
        assertEquals(0, result.getActive());
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void getByPersonId_ShouldReturnList() {
        when(reservationRepository.findByPersonIdAndActive(1L, 1)).thenReturn(Arrays.asList(reservation));
        List<ReservationEntity> result = reservationService.getByPersonId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getPassengersByReservationId_ShouldReturnList() {
        com.travel.app.entities.ReservationPassengerEntity rp = new com.travel.app.entities.ReservationPassengerEntity();
        when(reservationPassengerRepository.findByReservationIdAndActive(1L, 1)).thenReturn(Arrays.asList(rp));
        List<com.travel.app.entities.ReservationPassengerEntity> result = reservationService.getPassengersByReservationId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void countConfirmedPassengersByPackageId_ShouldReturnCount() {
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(5);
        int result = reservationService.countConfirmedPassengersByPackageId(1L);
        assertEquals(5, result);
    }

    @Test
    void changeStatus_InvalidEnum_ShouldThrowException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.changeStatus(1L, "INVALID", 2L);
        });
        assertTrue(exception.getMessage().contains("Estado inválido"));
    }

    @Test
    void createReservation_WhenPersonIdNotFound_ShouldThrowException() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setTourPackageId(1L);
        request.setPassengers(1);
        request.setPersonId(99L);

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(0);
        when(personService.getPersonById(99L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.createReservation(request, 2L);
        });
        assertTrue(exception.getMessage().contains("Persona no encontrada con ID"));
    }

    @Test
    void createReservation_WithDiscountsAndNewPassenger() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setTourPackageId(1L);
        request.setPassengers(1);
        request.setIdentification("NEW_ID");
        request.setSpecialRequests("Vegan");
        
        ReservationRequestDTO.DiscountDetailDTO discount = new ReservationRequestDTO.DiscountDetailDTO();
        discount.setName("Promo");
        request.setDiscountsDetail(Arrays.asList(discount));

        ReservationRequestDTO.PassengerDataDTO passengerData = new ReservationRequestDTO.PassengerDataDTO();
        passengerData.setIdentification("PASS_ID");
        request.setPassengersData(Arrays.asList(passengerData));

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(0);
        when(personService.findByIdentification("NEW_ID")).thenReturn(null);
        when(personService.findByIdentification("PASS_ID")).thenReturn(null);
        when(personService.savePerson(any(PersonEntity.class))).thenReturn(person);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity result = reservationService.createReservation(request, 2L);
        assertNotNull(result);
        verify(personService, times(2)).savePerson(any(PersonEntity.class));
        verify(reservationPassengerRepository, times(1)).save(any());
    }

    @Test
    void createReservation_WhenPassengerPersonIdNotFound_ShouldThrowException() {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setTourPackageId(1L);
        request.setPassengers(1);
        request.setPersonId(1L);
        
        ReservationRequestDTO.PassengerDataDTO passengerData = new ReservationRequestDTO.PassengerDataDTO();
        passengerData.setPersonId(99L);
        request.setPassengersData(Arrays.asList(passengerData));

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(reservationRepository.countConfirmedPassengersByPackageId(1L)).thenReturn(0);
        when(personService.getPersonById(1L)).thenReturn(person);
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);
        when(personService.getPersonById(99L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.createReservation(request, 2L);
        });
        assertTrue(exception.getMessage().contains("Persona no encontrada con ID"));
    }
}

package com.travel.app.services;

import com.travel.app.entities.ReservationPassengerEntity;
import com.travel.app.repositories.ReservationPassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationPassengerService {

    @Autowired
    ReservationPassengerRepository reservationPassengerRepository;

    // Listar solo pasajeros de reserva activos
    public List<ReservationPassengerEntity> getReservationPassengers() {
        return reservationPassengerRepository.findByActive(1);
    }

    // Guardar relación pasajero-reserva
    public ReservationPassengerEntity saveReservationPassenger(ReservationPassengerEntity rp) {
        return reservationPassengerRepository.save(rp);
    }

    // Buscar por ID (solo si está activo)
    public ReservationPassengerEntity getReservationPassengerById(Long id) {
        ReservationPassengerEntity rp = reservationPassengerRepository.findById(id).orElse(null);
        if (rp != null && rp.getActive() == 1) {
            return rp;
        }
        return null;
    }

    // Actualizar datos del pasajero en la reserva
    public ReservationPassengerEntity updateReservationPassenger(ReservationPassengerEntity rp) {
        return reservationPassengerRepository.save(rp);
    }

    // Borrado lógico (Desactivar)
    public boolean deleteReservationPassenger(Long id) throws Exception {
        try {
            ReservationPassengerEntity rp = reservationPassengerRepository.findById(id).orElse(null);
            if (rp != null) {
                rp.setActive(0);
                reservationPassengerRepository.save(rp);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new Exception("Error al desactivar el pasajero de la reserva: " + e.getMessage());
        }
    }
}
package com.travel.app.controllers;

import com.travel.app.dtos.ReservationRequestDTO;
import com.travel.app.entities.ReservationEntity;
import com.travel.app.enums.ReservationStatus;
import com.travel.app.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/")
    public ResponseEntity<List<ReservationEntity>> listReservations() {
        List<ReservationEntity> reservations = reservationService.getReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.getReservationById(id);
        return reservation != null ? ResponseEntity.ok(reservation) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/passengers")
    public ResponseEntity<?> getPassengersByReservationId(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getPassengersByReservationId(id));
    }

    // Nuevo endpoint para crear reserva con DTO
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createReservation(
            @RequestBody ReservationRequestDTO request,
            @RequestParam(defaultValue = "1") Long userId) {

        try {
            ReservationEntity reservation = reservationService.createReservation(request, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reserva creada exitosamente");
            response.put("data", reservation);
            response.put("reservationId", reservation.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/")
    public ResponseEntity<ReservationEntity> updateReservation(@RequestBody ReservationEntity reservation) {
        if (reservation.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        ReservationEntity updatedReservation = reservationService.updateReservation(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationEntity> changeStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(defaultValue = "1") Long userId) {
        ReservationEntity updated = reservationService.changeStatus(id, status, userId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<ReservationEntity>> getByPersonId(@PathVariable Long personId) {
        List<ReservationEntity> reservations = reservationService.getByPersonId(personId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/count-confirmed-passengers")
    public ResponseEntity<Integer> countConfirmedPassengersByPackageId(@RequestParam Long packageId) {
        return ResponseEntity.ok(reservationService.countConfirmedPassengersByPackageId(packageId));
    }

}

package com.travel.app.controllers;

import com.travel.app.services.ConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/confirmations")
@CrossOrigin("*")
public class ConfirmationController {

    @Autowired
    private ConfirmationService confirmationService;

    /** List all reservations (proxied from M4). */
    @GetMapping("/")
    public ResponseEntity<List<?>> getAllReservations() {
        return ResponseEntity.ok(confirmationService.getAllReservations());
    }

    /** Get a single reservation by ID (proxied from M4). */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        Map<?, ?> reservation = confirmationService.getReservationById(id);
        return reservation != null ? ResponseEntity.ok(reservation) : ResponseEntity.notFound().build();
    }

    /** Get passengers for a reservation (proxied from M4). */
    @GetMapping("/{id}/passengers")
    public ResponseEntity<List<?>> getPassengersByReservationId(@PathVariable Long id) {
        return ResponseEntity.ok(confirmationService.getPassengersByReservationId(id));
    }

    /**
     * Change the status of a reservation.
     * M6 delegates to M4 and then records the change in its own DB.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(defaultValue = "1") Long userId) {
        try {
            Map<?, ?> updated = confirmationService.changeStatus(id, status, userId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /** Get all reservations for a person (proxied from M4). */
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<?>> getReservationsByPersonId(@PathVariable Long personId) {
        return ResponseEntity.ok(confirmationService.getReservationsByPersonId(personId));
    }
}

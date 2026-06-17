package com.travel.app.controllers;

import com.travel.app.dtos.PaymentRequestDTO;
import com.travel.app.entities.PaymentEntity;
import com.travel.app.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/")
    public ResponseEntity<List<PaymentEntity>> listPayments() {
        List<PaymentEntity> payments = paymentService.getPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentEntity> getPaymentById(@PathVariable Long id) {
        PaymentEntity payment = paymentService.getPaymentById(id);
        return payment != null ? ResponseEntity.ok(payment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getPaymentByReservationId(@PathVariable Long reservationId) {
        PaymentEntity payment = paymentService.getPaymentByReservationId(reservationId);
        if (payment != null) {
            return ResponseEntity.ok(payment);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            PaymentEntity payment = paymentService.processPayment(request);
            response.put("success", true);
            response.put("message", "Pago procesado exitosamente");
            response.put("data", payment);
            response.put("transactionId", payment.getTransactionId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/")
    public ResponseEntity<PaymentEntity> savePayment(@RequestBody PaymentEntity payment) {
        PaymentEntity newPayment = paymentService.savePayment(payment);
        return ResponseEntity.ok(newPayment);
    }

    @PutMapping("/")
    public ResponseEntity<PaymentEntity> updatePayment(@RequestBody PaymentEntity payment) {
        PaymentEntity updatedPayment = paymentService.updatePayment(payment);
        return ResponseEntity.ok(updatedPayment);
    }
}
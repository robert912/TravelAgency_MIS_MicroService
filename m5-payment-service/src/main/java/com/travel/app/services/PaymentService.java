package com.travel.app.services;

import com.travel.app.dtos.PaymentRequestDTO;
import com.travel.app.entities.PaymentEntity;
import com.travel.app.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RestTemplate restTemplate;

    public List<PaymentEntity> getPayments() {
        List<PaymentEntity> payments = paymentRepository.findAll();
        payments.forEach(this::enrichPayment);
        return payments;
    }

    public PaymentEntity getPaymentById(Long id) {
        return paymentRepository.findById(id).map(this::enrichPayment).orElse(null);
    }

    public PaymentEntity getPaymentByReservationId(Long reservationId) {
        return paymentRepository.findByReservationId(reservationId).map(this::enrichPayment).orElse(null);
    }

    public boolean hasPayment(Long reservationId) {
        return paymentRepository.existsByReservationId(reservationId);
    }

    @Transactional
    public PaymentEntity processPayment(PaymentRequestDTO request) {
        Map<String, Object> reservation = getReservation(request.getReservationId());
        if (reservation == null) {
            throw new RuntimeException("Reserva no encontrada con ID: " + request.getReservationId());
        }

        Number active = (Number) reservation.get("active");
        if (active != null && active.intValue() != 1) {
            throw new RuntimeException("La reserva no esta activa");
        }

        String status = String.valueOf(reservation.get("status"));
        if (!"PENDIENTE".equals(status)) {
            throw new RuntimeException("Solo se pueden pagar reservas en estado PENDIENTE. Estado actual: " + status);
        }

        if (hasPayment(request.getReservationId())) {
            throw new RuntimeException("Esta reserva ya tiene un pago registrado");
        }

        validateCardData(request);

        BigDecimal amount = toBigDecimal(reservation.get("totalAmount"));
        if (amount == null) {
            amount = calculateAmountFromReservation(reservation);
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setReservationId(request.getReservationId());
        payment.setAmount(amount);
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "TARJETA_CREDITO");
        payment.setCardNumber(maskCardNumber(request.getCardNumber()));
        payment.setCardExpiration(request.getCardExpiration());
        payment.setCardCvv("***");
        payment.setTransactionId(generateTransactionId());
        payment.setCreatedByUserId(request.getUserId() != null ? request.getUserId() : 1);

        PaymentEntity saved = paymentRepository.save(payment);
        updateReservationStatus(request);
        saved.setReservation(getReservation(request.getReservationId()));
        return saved;
    }

    private BigDecimal calculateAmountFromReservation(Map<String, Object> reservation) {
        Number passengers = (Number) reservation.get("passengersCount");
        int passengersCount = passengers != null ? passengers.intValue() : 1;
        Object tourPackageObject = reservation.get("tourPackage");
        if (tourPackageObject instanceof Map<?, ?> tourPackage) {
            BigDecimal price = toBigDecimal(tourPackage.get("price"));
            if (price != null) {
                return price.multiply(BigDecimal.valueOf(passengersCount));
            }
        }
        return BigDecimal.ZERO;
    }

    private void updateReservationStatus(PaymentRequestDTO request) {
        Long userId = request.getUserId() != null ? request.getUserId() : 1L;
        String url = "http://m4-reservation-service/api/reservations/" + request.getReservationId()
                + "/status?status=PAGADA&userId=" + userId;
        restTemplate.put(url, null);
    }

    private Map<String, Object> getReservation(Long reservationId) {
        try {
            return restTemplate.getForObject("http://m4-reservation-service/api/reservations/" + reservationId,
                    Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private PaymentEntity enrichPayment(PaymentEntity payment) {
        if (payment != null) {
            payment.setReservation(getReservation(payment.getReservationId()));
        }
        return payment;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private void validateCardData(PaymentRequestDTO request) {
        String cardNumber = request.getCardNumber().replaceAll("\\s", "");
        if (!cardNumber.matches("\\d{16}")) {
            throw new RuntimeException("Numero de tarjeta invalido. Debe tener 16 digitos.");
        }

        String expiration = request.getCardExpiration();
        if (!expiration.matches("(0[1-9]|1[0-2])/(\\d{2}|\\d{4})")) {
            throw new RuntimeException("Fecha de expiracion invalida. Formato: MM/YY o MM/YYYY");
        }

        if (!request.getCardCvv().matches("\\d{3}")) {
            throw new RuntimeException("CVV invalido. Debe tener 3 digitos.");
        }

        if (request.getCardHolderName() == null || request.getCardHolderName().trim().isEmpty()) {
            throw new RuntimeException("Nombre del titular es requerido");
        }
    }

    private String maskCardNumber(String cardNumber) {
        String clean = cardNumber.replaceAll("\\s", "");
        if (clean.length() >= 4) {
            return "**** **** **** " + clean.substring(clean.length() - 4);
        }
        return "****";
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() +
                "-" + System.currentTimeMillis();
    }

    public PaymentEntity savePayment(PaymentEntity payment) {
        return enrichPayment(paymentRepository.save(payment));
    }

    public PaymentEntity updatePayment(PaymentEntity payment) {
        return enrichPayment(paymentRepository.save(payment));
    }
}

package com.travel.app.services;

import com.travel.app.dtos.PaymentRequestDTO;
import com.travel.app.entities.PaymentEntity;
import com.travel.app.entities.ReservationEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.enums.ReservationStatus;
import com.travel.app.repositories.PaymentRepository;
import com.travel.app.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentService paymentService;

    private ReservationEntity reservation;
    private PaymentRequestDTO request;
    private PaymentEntity payment;

    @BeforeEach
    void setUp() {
        reservation = new ReservationEntity();
        reservation.setId(1L);
        reservation.setActive(1);
        reservation.setStatus(ReservationStatus.PENDIENTE);
        reservation.setTotalAmount(new BigDecimal("1000.00"));
        
        TourPackageEntity tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);
        tourPackage.setPrice(new BigDecimal("1000.00"));
        reservation.setTourPackage(tourPackage);

        request = new PaymentRequestDTO();
        request.setReservationId(1L);
        request.setCardNumber("1234567812345678");
        request.setCardExpiration("12/25");
        request.setCardCvv("123");
        request.setCardHolderName("John Doe");

        payment = new PaymentEntity();
        payment.setId(1L);
    }

    @Test
    void processPayment_WhenValidRequest_ShouldCreatePayment() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(payment);

        PaymentEntity result = paymentService.processPayment(request);

        assertNotNull(result);
        assertEquals(ReservationStatus.PAGADA, reservation.getStatus());
        verify(reservationRepository, times(1)).save(reservation);
        verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
    }

    @Test
    void processPayment_WhenReservationNotActive_ShouldThrowException() {
        reservation.setActive(0);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("no está activa"));
    }

    @Test
    void processPayment_WhenReservationNotPending_ShouldThrowException() {
        reservation.setStatus(ReservationStatus.PAGADA);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("Solo se pueden pagar reservas en estado PENDIENTE"));
    }

    @Test
    void processPayment_WhenPaymentAlreadyExists_ShouldThrowException() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("ya tiene un pago registrado"));
    }

    @Test
    void processPayment_WhenInvalidCardNumber_ShouldThrowException() {
        request.setCardNumber("123");
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("Número de tarjeta inválido"));
    }

    @Test
    void processPayment_WhenInvalidExpiration_ShouldThrowException() {
        request.setCardExpiration("13/25"); // Invalid month
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("Fecha de expiración inválida"));
    }

    @Test
    void processPayment_WhenInvalidCVV_ShouldThrowException() {
        request.setCardCvv("12"); // Invalid CVV length
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("CVV inválido"));
    }

    @Test
    void processPayment_WhenMissingHolderName_ShouldThrowException() {
        request.setCardHolderName(""); // Empty name
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(paymentRepository.existsByReservationId(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });

        assertTrue(exception.getMessage().contains("Nombre del titular es requerido"));
    }

    @Test
    void getPayments_ShouldReturnList() {
        when(paymentRepository.findAll()).thenReturn(java.util.Arrays.asList(payment));
        java.util.List<PaymentEntity> result = paymentService.getPayments();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        PaymentEntity result = paymentService.getPaymentById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getPaymentByReservationId_ShouldReturnPayment() {
        when(paymentRepository.findByReservationId(1L)).thenReturn(Optional.of(payment));
        PaymentEntity result = paymentService.getPaymentByReservationId(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void savePayment_ShouldReturnPayment() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        PaymentEntity result = paymentService.savePayment(payment);
        assertNotNull(result);
    }

    @Test
    void updatePayment_ShouldReturnPayment() {
        when(paymentRepository.save(payment)).thenReturn(payment);
        PaymentEntity result = paymentService.updatePayment(payment);
        assertNotNull(result);
    }
}

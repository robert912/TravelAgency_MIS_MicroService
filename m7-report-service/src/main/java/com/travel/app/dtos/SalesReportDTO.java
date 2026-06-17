package com.travel.app.dtos;

import com.travel.app.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesReportDTO(
        Long reservationId,
        LocalDateTime operationDate,
        LocalDateTime reservationDate,
        LocalDateTime paymentDate,
        String clientName,
        String clientEmail,
        String packageName,
        String destination,
        Integer passengersCount,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        ReservationStatus status
) {
}

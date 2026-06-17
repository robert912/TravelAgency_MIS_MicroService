package com.travel.app.dtos;

import java.math.BigDecimal;

public record PackageRankingReportDTO(
        Long packageId,
        String packageName,
        String destination,
        Long reservationsCount,
        Long passengersCount,
        BigDecimal totalAmount
) {
}

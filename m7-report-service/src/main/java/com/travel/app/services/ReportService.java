package com.travel.app.services;

import com.travel.app.dtos.PackageRankingReportDTO;
import com.travel.app.dtos.SalesReportDTO;
import com.travel.app.enums.ReservationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * M7 Report Service — pure aggregation, no own database.
 * Calls M4 (reservations) and M5 (payments) via RestTemplate to generate reports.
 */
@Service
public class ReportService {

    private static final String M4_URL = "http://m4-reservation-service/api/reservations/";
    private static final String M5_URL = "http://m5-payment-service/api/payments/reservation/";

    @Autowired
    private RestTemplate restTemplate;

    // ─────────────────────────────────────────────────────────────────────
    //  Public API
    // ─────────────────────────────────────────────────────────────────────

    public List<SalesReportDTO> getSalesByPeriod(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        LocalDateTime from = startOfDay(startDate);
        LocalDateTime to   = endOfDay(endDate);

        List<Map<String, Object>> reservations = fetchAllReservations();

        return reservations.stream()
                .filter(r -> isActiveReservation(r))
                .filter(r -> isWithinRange(r, from, to))
                .map(r -> toSalesReportDTO(r))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SalesReportDTO::reservationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<PackageRankingReportDTO> getPackageRankingByPeriod(LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        LocalDateTime from = startOfDay(startDate);
        LocalDateTime to   = endOfDay(endDate);

        List<Map<String, Object>> reservations = fetchAllReservations();

        // Group by tourPackageId and aggregate
        Map<Long, List<Map<String, Object>>> byPackage = reservations.stream()
                .filter(r -> isActiveReservation(r))
                .filter(r -> isWithinRange(r, from, to))
                .filter(r -> r.get("tourPackageId") != null)
                .collect(Collectors.groupingBy(r -> toLong(r.get("tourPackageId"))));

        return byPackage.entrySet().stream()
                .map(e -> toPackageRankingDTO(e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PackageRankingReportDTO::reservationsCount).reversed())
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Private helpers
    // ─────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchAllReservations() {
        try {
            List<Map<String, Object>> list = restTemplate.exchange(
                    M4_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
            return list != null ? list : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchPaymentByReservationId(Long reservationId) {
        try {
            return restTemplate.getForObject(M5_URL + reservationId, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isActiveReservation(Map<String, Object> r) {
        Object active = r.get("active");
        if (active == null) return true; // treat null as active
        return toInt(active) == 1;
    }

    private boolean isWithinRange(Map<String, Object> r, LocalDateTime from, LocalDateTime to) {
        LocalDateTime reservationDate = parseDateTime(r.get("reservationDate"));
        if (reservationDate == null) return false;
        return !reservationDate.isBefore(from) && !reservationDate.isAfter(to);
    }

    private SalesReportDTO toSalesReportDTO(Map<String, Object> r) {
        try {
            Long reservationId = toLong(r.get("id"));
            LocalDateTime reservationDate = parseDateTime(r.get("reservationDate"));
            LocalDateTime updatedAt = parseDateTime(r.get("updatedAt"));
            Integer passengersCount = toInt(r.get("passengersCount"));
            BigDecimal totalAmount = toBigDecimal(r.get("totalAmount"));
            ReservationStatus status = parseStatus(r.get("status"));

            // Person data (enriched by M4)
            String clientName  = null;
            String clientEmail = null;
            Object personObj = r.get("person");
            if (personObj instanceof Map<?, ?> person) {
                clientName  = (String) person.get("fullName");
                clientEmail = (String) person.get("email");
            }

            // Package data (enriched by M4)
            String packageName = null;
            String destination = null;
            Object pkgObj = r.get("tourPackage");
            if (pkgObj instanceof Map<?, ?> pkg) {
                packageName = (String) pkg.get("name");
                destination = (String) pkg.get("destination");
            }

            // Payment data from M5
            LocalDateTime paymentDate = null;
            BigDecimal paidAmount = null;
            if (reservationId != null) {
                Map<String, Object> payment = fetchPaymentByReservationId(reservationId);
                if (payment != null) {
                    paymentDate = parseDateTime(payment.get("createdAt"));
                    paidAmount  = toBigDecimal(payment.get("amount"));
                }
            }

            return new SalesReportDTO(
                    reservationId,
                    updatedAt,        // operationDate
                    reservationDate,
                    paymentDate,
                    clientName,
                    clientEmail,
                    packageName,
                    destination,
                    passengersCount,
                    totalAmount,
                    paidAmount,
                    status
            );
        } catch (Exception e) {
            return null;
        }
    }

    private PackageRankingReportDTO toPackageRankingDTO(Long packageId, List<Map<String, Object>> reservations) {
        try {
            String packageName = null;
            String destination = null;

            // Get package info from first reservation's enriched tourPackage
            for (Map<String, Object> r : reservations) {
                Object pkgObj = r.get("tourPackage");
                if (pkgObj instanceof Map<?, ?> pkg) {
                    packageName = (String) pkg.get("name");
                    destination = (String) pkg.get("destination");
                    break;
                }
            }

            long reservationsCount = reservations.size();
            long passengersCount = reservations.stream()
                    .mapToLong(r -> toInt(r.get("passengersCount")))
                    .sum();
            BigDecimal totalAmount = reservations.stream()
                    .map(r -> toBigDecimal(r.get("totalAmount")))
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new PackageRankingReportDTO(
                    packageId,
                    packageName,
                    destination,
                    reservationsCount,
                    passengersCount,
                    totalAmount
            );
        } catch (Exception e) {
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Validation & type conversion
    // ─────────────────────────────────────────────────────────────────────

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Debe ingresar fecha de inicio y fecha de termino");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de termino");
        }
    }

    private LocalDateTime startOfDay(LocalDate date) { return date.atStartOfDay(); }
    private LocalDateTime endOfDay(LocalDate date)   { return date.atTime(LocalTime.MAX); }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.longValue();
        try { return Long.parseLong(value.toString()); } catch (Exception e) { return null; }
    }

    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number n) return n.intValue();
        try { return Integer.parseInt(value.toString()); } catch (Exception e) { return 0; }
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(value.toString()); } catch (Exception e) { return null; }
    }

    private LocalDateTime parseDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime ldt) return ldt;
        try {
            // Jackson typically serializes LocalDateTime as array or ISO string
            if (value instanceof List<?> arr) {
                // [year, month, day, hour, minute, second, nano]
                List<?> list = (List<?>) arr;
                int year   = toInt(list.get(0));
                int month  = toInt(list.get(1));
                int day    = toInt(list.get(2));
                int hour   = list.size() > 3 ? toInt(list.get(3)) : 0;
                int minute = list.size() > 4 ? toInt(list.get(4)) : 0;
                int second = list.size() > 5 ? toInt(list.get(5)) : 0;
                return LocalDateTime.of(year, month, day, hour, minute, second);
            }
            return LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }

    private ReservationStatus parseStatus(Object value) {
        if (value == null) return null;
        try { return ReservationStatus.valueOf(value.toString()); } catch (Exception e) { return null; }
    }
}

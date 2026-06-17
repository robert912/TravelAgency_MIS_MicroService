package com.travel.app.services;

import com.travel.app.dtos.PackageRankingReportDTO;
import com.travel.app.dtos.SalesReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        // Default: M4 returns empty list so tests that don't need data don't fail
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of()));
    }

    // ── Validation tests (no network calls needed) ──────────────────────

    @Test
    void getSalesByPeriodRejectsMissingStartDate() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getSalesByPeriod(null, LocalDate.of(2026, 5, 8))
        );
        assertEquals("Debe ingresar fecha de inicio y fecha de termino", ex.getMessage());
    }

    @Test
    void getSalesByPeriodRejectsMissingEndDate() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getSalesByPeriod(LocalDate.of(2026, 5, 1), null)
        );
        assertEquals("Debe ingresar fecha de inicio y fecha de termino", ex.getMessage());
    }

    @Test
    void getSalesByPeriodRejectsInvertedDateRange() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getSalesByPeriod(
                        LocalDate.of(2026, 5, 9),
                        LocalDate.of(2026, 5, 8)
                )
        );
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de termino", ex.getMessage());
    }

    @Test
    void getPackageRankingByPeriodRejectsMissingDates() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getPackageRankingByPeriod(null, LocalDate.of(2026, 5, 8))
        );
        assertEquals("Debe ingresar fecha de inicio y fecha de termino", ex.getMessage());
    }

    @Test
    void getPackageRankingByPeriodRejectsInvalidDateRange() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.getPackageRankingByPeriod(
                        LocalDate.of(2026, 5, 9),
                        LocalDate.of(2026, 5, 8)
                )
        );
        assertEquals("La fecha de inicio no puede ser posterior a la fecha de termino", ex.getMessage());
    }

    // ── Aggregation tests with mocked M4 data ───────────────────────────

    @Test
    void getSalesByPeriodReturnsEmptyListWhenNoReservations() {
        List<SalesReportDTO> result = reportService.getSalesByPeriod(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSalesByPeriodFiltersReservationsByDateRange() {
        // Reservation inside range
        Map<String, Object> inside = Map.of(
                "id", 1L,
                "reservationDate", List.of(2026, 5, 15, 10, 0, 0),
                "active", 1,
                "passengersCount", 2,
                "totalAmount", 500.0,
                "status", "PAGADA",
                "tourPackageId", 10L
        );
        // Reservation outside range
        Map<String, Object> outside = Map.of(
                "id", 2L,
                "reservationDate", List.of(2026, 4, 1, 10, 0, 0),
                "active", 1,
                "passengersCount", 1,
                "totalAmount", 200.0,
                "status", "PENDIENTE",
                "tourPackageId", 10L
        );

        when(restTemplate.exchange(
                contains("/api/reservations/"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(inside, outside)));

        when(restTemplate.getForObject(contains("/api/payments/reservation/"), eq(Map.class)))
                .thenReturn(null);

        List<SalesReportDTO> result = reportService.getSalesByPeriod(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).reservationId());
    }

    @Test
    void getPackageRankingByPeriodAggregatesCorrectly() {
        Map<String, Object> r1 = Map.of(
                "id", 1L,
                "reservationDate", List.of(2026, 5, 10, 10, 0, 0),
                "active", 1,
                "passengersCount", 3,
                "totalAmount", 900.0,
                "status", "PAGADA",
                "tourPackageId", 42L,
                "tourPackage", Map.of("name", "Patagonia Tour", "destination", "Chile")
        );
        Map<String, Object> r2 = Map.of(
                "id", 2L,
                "reservationDate", List.of(2026, 5, 20, 12, 0, 0),
                "active", 1,
                "passengersCount", 2,
                "totalAmount", 600.0,
                "status", "PAGADA",
                "tourPackageId", 42L,
                "tourPackage", Map.of("name", "Patagonia Tour", "destination", "Chile")
        );

        when(restTemplate.exchange(
                contains("/api/reservations/"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(r1, r2)));

        List<PackageRankingReportDTO> result = reportService.getPackageRankingByPeriod(
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        assertEquals(1, result.size());
        PackageRankingReportDTO dto = result.get(0);
        assertEquals(42L, dto.packageId());
        assertEquals("Patagonia Tour", dto.packageName());
        assertEquals(2L, dto.reservationsCount());
        assertEquals(5L, dto.passengersCount());
    }
}

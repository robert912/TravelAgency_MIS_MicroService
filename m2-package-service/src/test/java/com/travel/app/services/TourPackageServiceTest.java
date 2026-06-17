package com.travel.app.services;

import com.travel.app.entities.TourPackageEntity;
import com.travel.app.enums.PackageStatus;
import com.travel.app.repositories.TourPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourPackageServiceTest {

    @Mock
    private TourPackageRepository tourPackageRepository;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private TourPackageService tourPackageService;

    private TourPackageEntity tourPackage;

    @BeforeEach
    void setUp() {
        tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);
        tourPackage.setName("Test Package");
        tourPackage.setActive(1);
        tourPackage.setTotalSlots(20);
        tourPackage.setStatus(PackageStatus.DISPONIBLE);
        tourPackage.setStartDate(LocalDate.now().plusDays(10));
    }

    @Test
    void getTourPackages_ShouldReturnList() {
        when(tourPackageRepository.findAll()).thenReturn(Arrays.asList(tourPackage));
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        lenient().when(reservationService.countConfirmedPassengersByPackageId(1L)).thenReturn(5);

        List<TourPackageEntity> result = tourPackageService.getTourPackages();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tourPackageRepository, times(1)).findAll();
    }

    @Test
    void updatePackageStatusIfNeeded_WhenExpired_ShouldSetNoVigente() {
        tourPackage.setStartDate(LocalDate.now().minusDays(1)); // Expired

        boolean updated = tourPackageService.updatePackageStatusIfNeeded(tourPackage);

        assertTrue(updated);
        assertEquals(PackageStatus.NO_VIGENTE, tourPackage.getStatus());
        verify(tourPackageRepository, times(1)).save(tourPackage);
    }

    @Test
    void updatePackageStatusIfNeeded_WhenNoSlots_ShouldSetAgotado() {
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        when(reservationService.countConfirmedPassengersByPackageId(1L)).thenReturn(20); // 20 slots taken

        boolean updated = tourPackageService.updatePackageStatusIfNeeded(tourPackage);

        assertTrue(updated);
        assertEquals(PackageStatus.AGOTADO, tourPackage.getStatus());
        verify(tourPackageRepository, times(1)).save(tourPackage);
    }

    @Test
    void updatePackageStatusIfNeeded_WhenCancelled_ShouldNotChange() {
        tourPackage.setStatus(PackageStatus.CANCELADO);

        boolean updated = tourPackageService.updatePackageStatusIfNeeded(tourPackage);

        assertFalse(updated);
        verify(tourPackageRepository, never()).save(any());
    }

    @Test
    void getAvailableSlots_ShouldCalculateCorrectly() {
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        when(reservationService.countConfirmedPassengersByPackageId(1L)).thenReturn(8);

        int slots = tourPackageService.getAvailableSlots(1L);

        assertEquals(12, slots); // 20 - 8
    }

    @Test
    void saveTourPackage_ShouldReturnSaved() {
        when(tourPackageRepository.save(any(TourPackageEntity.class))).thenReturn(tourPackage);

        TourPackageEntity result = tourPackageService.saveTourPackage(tourPackage);

        assertNotNull(result);
        assertEquals(tourPackage.getId(), result.getId());
    }

    @Test
    void deleteTourPackage_ShouldSetInactive() throws Exception {
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        when(tourPackageRepository.save(any(TourPackageEntity.class))).thenReturn(tourPackage);

        boolean result = tourPackageService.deleteTourPackage(1L);

        assertTrue(result);
        assertEquals(0, tourPackage.getActive());
    }

    @Test
    void getTourPackagesActive_ShouldReturnList() {
        when(tourPackageRepository.findByActive(1)).thenReturn(Arrays.asList(tourPackage));
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        lenient().when(reservationService.countConfirmedPassengersByPackageId(1L)).thenReturn(5);

        List<TourPackageEntity> result = tourPackageService.getTourPackagesActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tourPackageRepository, times(1)).findByActive(1);
    }

    @Test
    void getTourPackageById_WhenDoesNotExist_ShouldReturnNull() {
        when(tourPackageRepository.findById(2L)).thenReturn(Optional.empty());
        TourPackageEntity result = tourPackageService.getTourPackageById(2L);
        assertNull(result);
    }

    @Test
    void updateTourPackage_ShouldReturnUpdated() {
        when(tourPackageRepository.save(any(TourPackageEntity.class))).thenReturn(tourPackage);
        TourPackageEntity result = tourPackageService.updateTourPackage(tourPackage);
        assertNotNull(result);
    }

    @Test
    void deleteTourPackage_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(tourPackageRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = tourPackageService.deleteTourPackage(2L);
        assertFalse(result);
    }

    @Test
    void deleteTourPackage_WhenThrowsException_ShouldThrowException() {
        when(tourPackageRepository.findById(1L)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            tourPackageService.deleteTourPackage(1L);
        });
        assertTrue(exception.getMessage().contains("Error al desactivar el paquete turístico"));
    }

    @Test
    void filterTourPackages_ShouldReturnList() {
        when(tourPackageRepository.findByFilters(any(), any(), any(), any(), any(), any()))
            .thenReturn(Arrays.asList(tourPackage));
        List<TourPackageEntity> result = tourPackageService.filterTourPackages(
            "Paris", null, null, null, null, null);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updatePackageStatusIfNeeded_WhenPackageIsNull_ShouldReturnFalse() {
        boolean result = tourPackageService.updatePackageStatusIfNeeded(null);
        assertFalse(result);
    }

    @Test
    void updatePackageStatusIfNeeded_WhenStatusIsSame_ShouldReturnFalse() {
        // Status is DISPONIBLE, and calculateAutomaticStatus will return DISPONIBLE
        when(tourPackageRepository.findById(1L)).thenReturn(Optional.of(tourPackage));
        when(reservationService.countConfirmedPassengersByPackageId(1L)).thenReturn(5);

        boolean updated = tourPackageService.updatePackageStatusIfNeeded(tourPackage);

        assertFalse(updated);
        verify(tourPackageRepository, never()).save(any());
    }
}

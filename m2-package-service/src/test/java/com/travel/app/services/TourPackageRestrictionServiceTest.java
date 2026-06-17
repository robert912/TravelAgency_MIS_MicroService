package com.travel.app.services;

import com.travel.app.entities.RestrictionEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.entities.TourPackageRestrictionEntity;
import com.travel.app.repositories.TourPackageRestrictionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourPackageRestrictionServiceTest {

    @Mock
    private TourPackageRestrictionRepository tourPackageRestrictionRepository;

    @Mock
    private TourPackageService tourPackageService;

    @Mock
    private RestrictionService restrictionService;

    @InjectMocks
    private TourPackageRestrictionService tourPackageRestrictionService;

    private TourPackageEntity tourPackage;
    private RestrictionEntity restriction;

    @BeforeEach
    void setUp() {
        tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);

        restriction = new RestrictionEntity();
        restriction.setId(10L);
    }

    @Test
    void syncPackageRestrictions_ShouldAddNewRestriction() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageRestrictionRepository.findByTourPackageId(1L)).thenReturn(Collections.emptyList());
        when(restrictionService.getRestrictionById(10L)).thenReturn(restriction);

        tourPackageRestrictionService.syncPackageRestrictions(1L, Arrays.asList(10L), 2L);

        verify(tourPackageRestrictionRepository, times(1)).save(any(TourPackageRestrictionEntity.class));
    }

    @Test
    void syncPackageRestrictions_ShouldReactivateExistingRestriction() {
        TourPackageRestrictionEntity relation = new TourPackageRestrictionEntity();
        relation.setRestriction(restriction);
        relation.setActive(0);
        
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageRestrictionRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        tourPackageRestrictionService.syncPackageRestrictions(1L, Arrays.asList(10L), 2L);

        assertEquals(1, relation.getActive());
        verify(tourPackageRestrictionRepository, times(1)).save(relation);
    }

    @Test
    void syncPackageRestrictions_WhenPackageNotFound_ShouldThrowException() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tourPackageRestrictionService.syncPackageRestrictions(1L, Arrays.asList(10L), 2L);
        });

        assertTrue(exception.getMessage().contains("Paquete no encontrado con ID"));
    }

    @Test
    void syncPackageRestrictions_ShouldDeactivateRemovedRestriction() {
        TourPackageRestrictionEntity relation = new TourPackageRestrictionEntity();
        relation.setRestriction(restriction); // ID 10
        relation.setActive(1);

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageRestrictionRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        // Sync with an empty list to trigger deactivation of ID 10
        tourPackageRestrictionService.syncPackageRestrictions(1L, Collections.emptyList(), 2L);

        assertEquals(0, relation.getActive()); // Should be deactivated
        verify(tourPackageRestrictionRepository, times(1)).save(relation);
    }
}

package com.travel.app.services;

import com.travel.app.entities.ConditionEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.entities.TourPackageConditionEntity;
import com.travel.app.repositories.TourPackageConditionRepository;
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
public class TourPackageConditionServiceTest {

    @Mock
    private TourPackageConditionRepository tourPackageConditionRepository;

    @Mock
    private TourPackageService tourPackageService;

    @Mock
    private ConditionService conditionService;

    @InjectMocks
    private TourPackageConditionService tourPackageConditionService;

    private TourPackageEntity tourPackage;
    private ConditionEntity condition;

    @BeforeEach
    void setUp() {
        tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);

        condition = new ConditionEntity();
        condition.setId(10L);
    }

    @Test
    void syncPackageConditions_ShouldAddNewCondition() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageConditionRepository.findByTourPackageId(1L)).thenReturn(Collections.emptyList());
        when(conditionService.getConditionById(10L)).thenReturn(condition);

        tourPackageConditionService.syncPackageConditions(1L, Arrays.asList(10L), 2L);

        verify(tourPackageConditionRepository, times(1)).save(any(TourPackageConditionEntity.class));
    }

    @Test
    void syncPackageConditions_ShouldReactivateExistingCondition() {
        TourPackageConditionEntity relation = new TourPackageConditionEntity();
        relation.setCondition(condition);
        relation.setActive(0);
        
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageConditionRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        tourPackageConditionService.syncPackageConditions(1L, Arrays.asList(10L), 2L);

        assertEquals(1, relation.getActive());
        verify(tourPackageConditionRepository, times(1)).save(relation);
    }

    @Test
    void syncPackageConditions_WhenPackageNotFound_ShouldThrowException() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tourPackageConditionService.syncPackageConditions(1L, Arrays.asList(10L), 2L);
        });

        assertTrue(exception.getMessage().contains("Paquete no encontrado con ID"));
    }

    @Test
    void syncPackageConditions_ShouldDeactivateRemovedCondition() {
        TourPackageConditionEntity relation = new TourPackageConditionEntity();
        relation.setCondition(condition); // ID 10
        relation.setActive(1);

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageConditionRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        // Sync with an empty list to trigger deactivation of ID 10
        tourPackageConditionService.syncPackageConditions(1L, Collections.emptyList(), 2L);

        assertEquals(0, relation.getActive()); // Should be deactivated
        verify(tourPackageConditionRepository, times(1)).save(relation);
    }
}

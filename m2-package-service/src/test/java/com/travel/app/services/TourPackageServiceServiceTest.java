package com.travel.app.services;

import com.travel.app.entities.ServiceEntity;
import com.travel.app.entities.TourPackageEntity;
import com.travel.app.entities.TourPackageServiceEntity;
import com.travel.app.repositories.TourPackageServiceRepository;
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
public class TourPackageServiceServiceTest {

    @Mock
    private TourPackageServiceRepository tourPackageServiceRepository;

    @Mock
    private TourPackageService tourPackageService;

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private TourPackageServiceService tourPackageServiceService;

    private TourPackageEntity tourPackage;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        tourPackage = new TourPackageEntity();
        tourPackage.setId(1L);

        service = new ServiceEntity();
        service.setId(10L);
    }

    @Test
    void syncPackageServices_WhenPackageNotFound_ShouldThrowException() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tourPackageServiceService.syncPackageServices(1L, Arrays.asList(10L), 2L);
        });

        assertTrue(exception.getMessage().contains("Paquete no encontrado"));
    }

    @Test
    void syncPackageServices_WhenServiceNotFound_ShouldThrowException() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageServiceRepository.findByTourPackageId(1L)).thenReturn(Collections.emptyList());
        when(serviceService.getServiceById(10L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            tourPackageServiceService.syncPackageServices(1L, Arrays.asList(10L), 2L);
        });

        assertTrue(exception.getMessage().contains("Servicio no encontrado"));
    }

    @Test
    void syncPackageServices_ShouldAddNewService() {
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageServiceRepository.findByTourPackageId(1L)).thenReturn(Collections.emptyList());
        when(serviceService.getServiceById(10L)).thenReturn(service);

        tourPackageServiceService.syncPackageServices(1L, Arrays.asList(10L), 2L);

        verify(tourPackageServiceRepository, times(1)).save(any(TourPackageServiceEntity.class));
    }

    @Test
    void syncPackageServices_ShouldReactivateExistingService() {
        TourPackageServiceEntity relation = new TourPackageServiceEntity();
        relation.setService(service);
        relation.setActive(0);
        
        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageServiceRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        tourPackageServiceService.syncPackageServices(1L, Arrays.asList(10L), 2L);

        assertEquals(1, relation.getActive());
        verify(tourPackageServiceRepository, times(1)).save(relation);
    }

    @Test
    void syncPackageServices_ShouldDeactivateRemovedService() {
        TourPackageServiceEntity relation = new TourPackageServiceEntity();
        relation.setService(service);
        relation.setActive(1);

        when(tourPackageService.getTourPackageById(1L)).thenReturn(tourPackage);
        when(tourPackageServiceRepository.findByTourPackageId(1L)).thenReturn(Arrays.asList(relation));

        // Sync with an empty list should deactivate the existing service
        tourPackageServiceService.syncPackageServices(1L, Collections.emptyList(), 2L);

        assertEquals(0, relation.getActive());
        verify(tourPackageServiceRepository, times(1)).save(relation);
    }
}

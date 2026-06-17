package com.travel.app.services;

import com.travel.app.entities.ServiceEntity;
import com.travel.app.repositories.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ServiceEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getServices_ShouldReturnList() {
        when(serviceRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<ServiceEntity> result = serviceService.getServices();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void getServicesActive_ShouldReturnOnlyActive() {
        when(serviceRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        List<ServiceEntity> result = serviceService.getServicesActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findByActive(1);
    }

    @Test
    void saveService_ShouldReturnSaved() {
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(entity);
        ServiceEntity result = serviceService.saveService(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(serviceRepository, times(1)).save(entity);
    }

    @Test
    void getServiceById_WhenExists_ShouldReturn() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(entity));
        ServiceEntity result = serviceService.getServiceById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(serviceRepository, times(1)).findById(1L);
    }

    @Test
    void updateService_ShouldReturnUpdated() {
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(entity);
        ServiceEntity result = serviceService.updateService(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(serviceRepository, times(1)).save(entity);
    }

    @Test
    void getServiceById_WhenDoesNotExist_ShouldReturnNull() {
        when(serviceRepository.findById(2L)).thenReturn(Optional.empty());
        ServiceEntity result = serviceService.getServiceById(2L);
        assertNull(result);
        verify(serviceRepository, times(1)).findById(2L);
    }
}

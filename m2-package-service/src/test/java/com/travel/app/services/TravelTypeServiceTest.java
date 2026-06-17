package com.travel.app.services;

import com.travel.app.entities.TravelTypeEntity;
import com.travel.app.repositories.TravelTypeRepository;
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
public class TravelTypeServiceTest {

    @Mock
    private TravelTypeRepository travelTypeRepository;

    @InjectMocks
    private TravelTypeService travelTypeService;

    private TravelTypeEntity entity;

    @BeforeEach
    void setUp() {
        entity = new TravelTypeEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getTravelTypes_ShouldReturnList() {
        when(travelTypeRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<TravelTypeEntity> result = travelTypeService.getTravelTypes();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(travelTypeRepository, times(1)).findAll();
    }

    @Test
    void getTravelTypesActive_ShouldReturnOnlyActive() {
        when(travelTypeRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        List<TravelTypeEntity> result = travelTypeService.getTravelTypesActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(travelTypeRepository, times(1)).findByActive(1);
    }

    @Test
    void saveTravelType_ShouldReturnSaved() {
        when(travelTypeRepository.save(any(TravelTypeEntity.class))).thenReturn(entity);
        TravelTypeEntity result = travelTypeService.saveTravelType(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(travelTypeRepository, times(1)).save(entity);
    }

    @Test
    void getTravelTypeById_WhenExists_ShouldReturn() {
        when(travelTypeRepository.findById(1L)).thenReturn(Optional.of(entity));
        TravelTypeEntity result = travelTypeService.getTravelTypeById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(travelTypeRepository, times(1)).findById(1L);
    }

    @Test
    void updateTravelType_ShouldReturnUpdated() {
        when(travelTypeRepository.save(any(TravelTypeEntity.class))).thenReturn(entity);
        TravelTypeEntity result = travelTypeService.updateTravelType(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(travelTypeRepository, times(1)).save(entity);
    }

    @Test
    void getTravelTypeById_WhenDoesNotExist_ShouldReturnNull() {
        when(travelTypeRepository.findById(2L)).thenReturn(Optional.empty());
        TravelTypeEntity result = travelTypeService.getTravelTypeById(2L);
        assertNull(result);
        verify(travelTypeRepository, times(1)).findById(2L);
    }
}

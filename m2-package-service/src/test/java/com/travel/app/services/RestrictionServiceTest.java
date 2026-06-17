package com.travel.app.services;

import com.travel.app.entities.RestrictionEntity;
import com.travel.app.repositories.RestrictionRepository;
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
public class RestrictionServiceTest {

    @Mock
    private RestrictionRepository restrictionRepository;

    @InjectMocks
    private RestrictionService restrictionService;

    private RestrictionEntity entity;

    @BeforeEach
    void setUp() {
        entity = new RestrictionEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getRestrictions_ShouldReturnList() {
        when(restrictionRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<RestrictionEntity> result = restrictionService.getRestrictions();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restrictionRepository, times(1)).findAll();
    }

    @Test
    void getRestrictionsActive_ShouldReturnOnlyActive() {
        when(restrictionRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        List<RestrictionEntity> result = restrictionService.getRestrictionsActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(restrictionRepository, times(1)).findByActive(1);
    }

    @Test
    void saveRestriction_ShouldReturnSaved() {
        when(restrictionRepository.save(any(RestrictionEntity.class))).thenReturn(entity);
        RestrictionEntity result = restrictionService.saveRestriction(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(restrictionRepository, times(1)).save(entity);
    }

    @Test
    void getRestrictionById_WhenExists_ShouldReturn() {
        when(restrictionRepository.findById(1L)).thenReturn(Optional.of(entity));
        RestrictionEntity result = restrictionService.getRestrictionById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(restrictionRepository, times(1)).findById(1L);
    }

    @Test
    void updateRestriction_ShouldReturnUpdated() {
        when(restrictionRepository.save(any(RestrictionEntity.class))).thenReturn(entity);
        RestrictionEntity result = restrictionService.updateRestriction(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(restrictionRepository, times(1)).save(entity);
    }

    @Test
    void deleteRestriction_WhenExists_ShouldSetInactiveAndReturnTrue() throws Exception {
        when(restrictionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(restrictionRepository.save(any(RestrictionEntity.class))).thenReturn(entity);
        boolean result = restrictionService.deleteRestriction(1L);
        assertTrue(result);
        assertEquals(0, entity.getActive());
        verify(restrictionRepository, times(1)).findById(1L);
        verify(restrictionRepository, times(1)).save(entity);
    }

    @Test
    void getRestrictionById_WhenDoesNotExist_ShouldReturnNull() {
        when(restrictionRepository.findById(2L)).thenReturn(Optional.empty());
        RestrictionEntity result = restrictionService.getRestrictionById(2L);
        assertNull(result);
        verify(restrictionRepository, times(1)).findById(2L);
    }

    @Test
    void deleteRestriction_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(restrictionRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = restrictionService.deleteRestriction(2L);
        assertFalse(result);
        verify(restrictionRepository, times(1)).findById(2L);
        verify(restrictionRepository, never()).save(any());
    }

    @Test
    void deleteRestriction_WhenThrowsException_ShouldThrowException() {
        when(restrictionRepository.findById(1L)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            restrictionService.deleteRestriction(1L);
        });
        assertTrue(exception.getMessage().contains("Error al desactivar la restricción"));
    }
}

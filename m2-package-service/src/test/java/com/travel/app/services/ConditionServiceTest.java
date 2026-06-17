package com.travel.app.services;

import com.travel.app.entities.ConditionEntity;
import com.travel.app.repositories.ConditionRepository;
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
public class ConditionServiceTest {

    @Mock
    private ConditionRepository conditionRepository;

    @InjectMocks
    private ConditionService conditionService;

    private ConditionEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ConditionEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getConditions_ShouldReturnList() {
        when(conditionRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<ConditionEntity> result = conditionService.getConditions();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(conditionRepository, times(1)).findAll();
    }

    @Test
    void getConditionsActive_ShouldReturnOnlyActive() {
        when(conditionRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        List<ConditionEntity> result = conditionService.getConditionsActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(conditionRepository, times(1)).findByActive(1);
    }

    @Test
    void saveCondition_ShouldReturnSaved() {
        when(conditionRepository.save(any(ConditionEntity.class))).thenReturn(entity);
        ConditionEntity result = conditionService.saveCondition(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(conditionRepository, times(1)).save(entity);
    }

    @Test
    void getConditionById_WhenExists_ShouldReturn() {
        when(conditionRepository.findById(1L)).thenReturn(Optional.of(entity));
        ConditionEntity result = conditionService.getConditionById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(conditionRepository, times(1)).findById(1L);
    }

    @Test
    void updateCondition_ShouldReturnUpdated() {
        when(conditionRepository.save(any(ConditionEntity.class))).thenReturn(entity);
        ConditionEntity result = conditionService.updateCondition(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(conditionRepository, times(1)).save(entity);
    }

    @Test
    void deleteCondition_WhenExists_ShouldSetInactiveAndReturnTrue() throws Exception {
        when(conditionRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(conditionRepository.save(any(ConditionEntity.class))).thenReturn(entity);
        boolean result = conditionService.deleteCondition(1L);
        assertTrue(result);
        assertEquals(0, entity.getActive());
        verify(conditionRepository, times(1)).findById(1L);
        verify(conditionRepository, times(1)).save(entity);
    }

    @Test
    void getConditionById_WhenDoesNotExist_ShouldReturnNull() {
        when(conditionRepository.findById(2L)).thenReturn(Optional.empty());
        ConditionEntity result = conditionService.getConditionById(2L);
        assertNull(result);
        verify(conditionRepository, times(1)).findById(2L);
    }

    @Test
    void deleteCondition_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(conditionRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = conditionService.deleteCondition(2L);
        assertFalse(result);
        verify(conditionRepository, times(1)).findById(2L);
        verify(conditionRepository, never()).save(any());
    }

    @Test
    void deleteCondition_WhenThrowsException_ShouldThrowException() {
        when(conditionRepository.findById(1L)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            conditionService.deleteCondition(1L);
        });
        assertTrue(exception.getMessage().contains("Error al desactivar la condición"));
    }
}

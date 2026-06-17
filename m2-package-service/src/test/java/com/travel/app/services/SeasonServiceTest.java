package com.travel.app.services;

import com.travel.app.entities.SeasonEntity;
import com.travel.app.repositories.SeasonRepository;
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
public class SeasonServiceTest {

    @Mock
    private SeasonRepository seasonRepository;

    @InjectMocks
    private SeasonService seasonService;

    private SeasonEntity entity;

    @BeforeEach
    void setUp() {
        entity = new SeasonEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getSeasons_ShouldReturnList() {
        when(seasonRepository.findAll()).thenReturn(Arrays.asList(entity));
        List<SeasonEntity> result = seasonService.getSeasons();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(seasonRepository, times(1)).findAll();
    }

    @Test
    void getSeasonsActive_ShouldReturnOnlyActive() {
        when(seasonRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        List<SeasonEntity> result = seasonService.getSeasonsActive();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(seasonRepository, times(1)).findByActive(1);
    }

    @Test
    void saveSeason_ShouldReturnSaved() {
        when(seasonRepository.save(any(SeasonEntity.class))).thenReturn(entity);
        SeasonEntity result = seasonService.saveSeason(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(seasonRepository, times(1)).save(entity);
    }

    @Test
    void getSeasonById_WhenExists_ShouldReturn() {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(entity));
        SeasonEntity result = seasonService.getSeasonById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(seasonRepository, times(1)).findById(1L);
    }

    @Test
    void updateSeason_ShouldReturnUpdated() {
        when(seasonRepository.save(any(SeasonEntity.class))).thenReturn(entity);
        SeasonEntity result = seasonService.updateSeason(entity);
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(seasonRepository, times(1)).save(entity);
    }

    @Test
    void deleteSeason_WhenExists_ShouldSetInactiveAndReturnTrue() throws Exception {
        when(seasonRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(seasonRepository.save(any(SeasonEntity.class))).thenReturn(entity);
        boolean result = seasonService.deleteSeason(1L);
        assertTrue(result);
        assertEquals(0, entity.getActive());
        verify(seasonRepository, times(1)).findById(1L);
        verify(seasonRepository, times(1)).save(entity);
    }

    @Test
    void getSeasonById_WhenDoesNotExist_ShouldReturnNull() {
        when(seasonRepository.findById(2L)).thenReturn(Optional.empty());
        SeasonEntity result = seasonService.getSeasonById(2L);
        assertNull(result);
        verify(seasonRepository, times(1)).findById(2L);
    }

    @Test
    void deleteSeason_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(seasonRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = seasonService.deleteSeason(2L);
        assertFalse(result);
        verify(seasonRepository, times(1)).findById(2L);
        verify(seasonRepository, never()).save(any());
    }

    @Test
    void deleteSeason_WhenThrowsException_ShouldThrowException() {
        when(seasonRepository.findById(1L)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            seasonService.deleteSeason(1L);
        });
        assertTrue(exception.getMessage().contains("Error al desactivar la temporada"));
    }
}

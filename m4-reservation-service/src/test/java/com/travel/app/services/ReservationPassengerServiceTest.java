package com.travel.app.services;

import com.travel.app.entities.ReservationPassengerEntity;
import com.travel.app.repositories.ReservationPassengerRepository;
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
public class ReservationPassengerServiceTest {

    @Mock
    private ReservationPassengerRepository reservationPassengerRepository;

    @InjectMocks
    private ReservationPassengerService reservationPassengerService;

    private ReservationPassengerEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ReservationPassengerEntity();
        entity.setId(1L);
        entity.setActive(1);
    }

    @Test
    void getReservationPassengers_ShouldReturnList() {
        when(reservationPassengerRepository.findByActive(1)).thenReturn(Arrays.asList(entity));
        
        List<ReservationPassengerEntity> result = reservationPassengerService.getReservationPassengers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationPassengerRepository, times(1)).findByActive(1);
    }

    @Test
    void saveReservationPassenger_ShouldReturnSaved() {
        when(reservationPassengerRepository.save(any(ReservationPassengerEntity.class))).thenReturn(entity);
        
        ReservationPassengerEntity result = reservationPassengerService.saveReservationPassenger(entity);
        
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(reservationPassengerRepository, times(1)).save(entity);
    }

    @Test
    void getReservationPassengerById_WhenExists_ShouldReturn() {
        when(reservationPassengerRepository.findById(1L)).thenReturn(Optional.of(entity));
        
        ReservationPassengerEntity result = reservationPassengerService.getReservationPassengerById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reservationPassengerRepository, times(1)).findById(1L);
    }

    @Test
    void updateReservationPassenger_ShouldReturnUpdated() {
        when(reservationPassengerRepository.save(any(ReservationPassengerEntity.class))).thenReturn(entity);
        
        ReservationPassengerEntity result = reservationPassengerService.updateReservationPassenger(entity);
        
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        verify(reservationPassengerRepository, times(1)).save(entity);
    }

    @Test
    void deleteReservationPassenger_WhenExists_ShouldSetInactiveAndReturnTrue() throws Exception {
        when(reservationPassengerRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reservationPassengerRepository.save(any(ReservationPassengerEntity.class))).thenReturn(entity);
        
        boolean result = reservationPassengerService.deleteReservationPassenger(1L);
        
        assertTrue(result);
        assertEquals(0, entity.getActive());
        verify(reservationPassengerRepository, times(1)).findById(1L);
        verify(reservationPassengerRepository, times(1)).save(entity);
    }

    @Test
    void getReservationPassengerById_WhenDoesNotExist_ShouldReturnNull() {
        when(reservationPassengerRepository.findById(2L)).thenReturn(Optional.empty());
        ReservationPassengerEntity result = reservationPassengerService.getReservationPassengerById(2L);
        assertNull(result);
        verify(reservationPassengerRepository, times(1)).findById(2L);
    }

    @Test
    void deleteReservationPassenger_WhenDoesNotExist_ShouldReturnFalse() throws Exception {
        when(reservationPassengerRepository.findById(2L)).thenReturn(Optional.empty());
        boolean result = reservationPassengerService.deleteReservationPassenger(2L);
        assertFalse(result);
        verify(reservationPassengerRepository, times(1)).findById(2L);
        verify(reservationPassengerRepository, never()).save(any());
    }

    @Test
    void deleteReservationPassenger_WhenThrowsException_ShouldThrowException() {
        when(reservationPassengerRepository.findById(1L)).thenThrow(new RuntimeException("DB Error"));
        Exception exception = assertThrows(Exception.class, () -> {
            reservationPassengerService.deleteReservationPassenger(1L);
        });
        assertTrue(exception.getMessage().contains("Error al desactivar el pasajero"));
    }
}

package com.travel.app.repositories;

import com.travel.app.entities.ReservationPassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ReservationPassengerRepository extends JpaRepository<ReservationPassengerEntity, Long> {

    List<ReservationPassengerEntity> findByActive(Integer active);

    List<ReservationPassengerEntity> findByReservationId(Long reservationId);

    List<ReservationPassengerEntity> findByReservationIdAndActive(Long reservationId, Integer active);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReservationPassengerEntity rp WHERE rp.reservation.id = :reservationId")
    void deleteByReservationId(@Param("reservationId") Long reservationId);
}
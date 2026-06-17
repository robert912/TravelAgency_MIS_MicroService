package com.travel.app.repositories;

import com.travel.app.entities.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByReservationId(Long reservationId);
    boolean existsByReservationId(Long reservationId);
}
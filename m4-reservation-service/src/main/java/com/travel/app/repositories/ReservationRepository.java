package com.travel.app.repositories;

import com.travel.app.entities.ReservationEntity;
import com.travel.app.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByActive(Integer active);

    List<ReservationEntity> findByPersonIdAndActive(Long personId, Integer active);

    List<ReservationEntity> findByTourPackageIdAndActive(Long tourPackageId, Integer active);

    List<ReservationEntity> findByStatusAndActive(ReservationStatus status, Integer active);

    @Query("SELECT COALESCE(SUM(r.passengersCount), 0) FROM ReservationEntity r " +
            "WHERE r.tourPackageId = :packageId " +
            "AND r.status IN :statuses " +
            "AND r.active = 1")
    Integer countPassengersByPackageIdAndStatuses(@Param("packageId") Long packageId,
                                                  @Param("statuses") List<ReservationStatus> statuses);

    default Integer countConfirmedPassengersByPackageId(Long packageId) {
        return countPassengersByPackageIdAndStatuses(packageId,
                List.of(ReservationStatus.PENDIENTE, ReservationStatus.PAGADA));
    }
}

package com.travel.app.repositories;

import com.travel.app.entities.TourPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourPackageRepository extends JpaRepository<TourPackageEntity,Long> {
    List<TourPackageEntity> findByActive(Integer active);


    @Query("SELECT t FROM TourPackageEntity t WHERE t.active = 1 " +
            "AND (:destination IS NULL OR (" +
                "LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%')) OR " +
                "LOWER(t.name) LIKE LOWER(CONCAT('%', :destination, '%'))" +
            ")) " +
            "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR t.price <= :maxPrice) " +
            "AND (:startDate IS NULL OR t.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.endDate <= :endDate) " +
            "AND (:travelTypeId IS NULL OR t.travelType.id = :travelTypeId)")
    List<TourPackageEntity> findByFilters(
            @Param("destination") String destination,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("travelTypeId") Long travelTypeId
    );
}

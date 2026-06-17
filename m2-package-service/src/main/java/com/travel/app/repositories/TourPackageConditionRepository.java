package com.travel.app.repositories;

import com.travel.app.entities.TourPackageConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPackageConditionRepository extends JpaRepository<TourPackageConditionEntity,Long> {
    List<TourPackageConditionEntity> findByActive(Integer active);

    // Buscar por ID de paquete
    List<TourPackageConditionEntity> findByTourPackageId(Long tourPackageId);

}

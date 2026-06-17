package com.travel.app.repositories;

import com.travel.app.entities.TourPackageRestrictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPackageRestrictionRepository extends JpaRepository<TourPackageRestrictionEntity,Long> {
    List<TourPackageRestrictionEntity> findByActive(Integer active);

    // Buscar por ID de paquete
    List<TourPackageRestrictionEntity> findByTourPackageId(Long tourPackageId);

}

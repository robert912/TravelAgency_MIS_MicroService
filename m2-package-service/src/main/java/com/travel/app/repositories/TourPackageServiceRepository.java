package com.travel.app.repositories;

import com.travel.app.entities.TourPackageServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPackageServiceRepository extends JpaRepository<TourPackageServiceEntity,Long> {
    List<TourPackageServiceEntity> findByActive(Integer active);

    // Buscar por ID de paquete
    List<TourPackageServiceEntity> findByTourPackageId(Long tourPackageId);

}

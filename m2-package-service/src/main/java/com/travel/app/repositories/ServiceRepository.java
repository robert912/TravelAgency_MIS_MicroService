package com.travel.app.repositories;

import com.travel.app.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity,Long> {
    List<ServiceEntity> findByActive(Integer active);
}

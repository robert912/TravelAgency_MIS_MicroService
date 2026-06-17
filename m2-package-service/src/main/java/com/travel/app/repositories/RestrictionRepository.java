package com.travel.app.repositories;

import com.travel.app.entities.RestrictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestrictionRepository extends JpaRepository<RestrictionEntity,Long> {
    List<RestrictionEntity> findByActive(Integer active);
}

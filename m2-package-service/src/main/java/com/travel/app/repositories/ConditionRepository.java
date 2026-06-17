package com.travel.app.repositories;

import com.travel.app.entities.ConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionRepository extends JpaRepository<ConditionEntity,Long> {
    List<ConditionEntity> findByActive(Integer active);
}

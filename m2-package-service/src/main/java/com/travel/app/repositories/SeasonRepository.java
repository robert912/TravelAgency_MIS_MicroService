package com.travel.app.repositories;

import com.travel.app.entities.SeasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRepository extends JpaRepository<SeasonEntity,Long> {
    List<SeasonEntity> findByActive(Integer active);
}

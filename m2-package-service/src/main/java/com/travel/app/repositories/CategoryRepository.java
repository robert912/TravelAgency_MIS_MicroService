package com.travel.app.repositories;

import com.travel.app.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    List<CategoryEntity> findByActive(Integer active);
}

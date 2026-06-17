package com.travel.app.repositories;

import com.travel.app.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity,Long> {
    List<PersonEntity> findByActive(Integer active);
    PersonEntity findByIdentification(String identification, Integer active);
    PersonEntity findByEmail(String email, Integer active);
}

package com.travel.app.services;

import com.travel.app.entities.PersonEntity;
import com.travel.app.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public List<PersonEntity> getPersons() {
        return (List<PersonEntity>) personRepository.findAll();
    }

    public List<PersonEntity> getPersonsActive() {
        return (List<PersonEntity>) personRepository.findByActive(1);
    }

    public PersonEntity findByIdentification(String identification) {
        return personRepository.findByIdentificationAndActive(identification, 1);
    }

    public PersonEntity findByEmail(String email) {
        return personRepository.findByEmailAndActive(email, 1);
    }

    public PersonEntity savePerson(PersonEntity person) {
        return personRepository.save(person);
    }

    public PersonEntity getPersonById(Long id) {
        return personRepository.findById(id).orElse(null);
    }

    public PersonEntity updatePerson(PersonEntity person) {
        return personRepository.save(person);
    }
}

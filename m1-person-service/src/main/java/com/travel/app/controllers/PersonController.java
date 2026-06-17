package com.travel.app.controllers;

import com.travel.app.entities.PersonEntity;
import com.travel.app.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin("*")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/")
    public ResponseEntity<List<PersonEntity>> listPersons() {
        List<PersonEntity> persons = personService.getPersons();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PersonEntity>> listPersonsActive() {
        List<PersonEntity> persons = personService.getPersonsActive();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonEntity> getPersonById(@PathVariable Long id) {
        PersonEntity person = personService.getPersonById(id);
        return person != null ? ResponseEntity.ok(person) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPerson(@RequestParam String query) {
        PersonEntity person = personService.findByIdentification(query);
        if (person == null) {
            person = personService.findByEmail(query);
        }
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/")
    public ResponseEntity<PersonEntity> savePerson(@RequestBody PersonEntity person) {
        PersonEntity personNew = personService.savePerson(person);
        return ResponseEntity.ok(personNew);
    }

    @PutMapping("/")
    public ResponseEntity<PersonEntity> updatePerson(@RequestBody PersonEntity person) {
        PersonEntity personUpdated = personService.updatePerson(person);
        return ResponseEntity.ok(personUpdated);
    }
}

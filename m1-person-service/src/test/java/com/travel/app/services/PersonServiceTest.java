package com.travel.app.services;

import com.travel.app.entities.PersonEntity;
import com.travel.app.repositories.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private PersonEntity person;

    @BeforeEach
    void setUp() {
        person = new PersonEntity();
        person.setId(1L);
        person.setFullName("John Doe");
        person.setIdentification("123456789");
        person.setEmail("john@example.com");
        person.setActive(1);
    }

    @Test
    void getPersons_ShouldReturnListOfPersons() {
        when(personRepository.findAll()).thenReturn(Arrays.asList(person));

        List<PersonEntity> result = personService.getPersons();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void getPersonsActive_ShouldReturnOnlyActivePersons() {
        when(personRepository.findByActive(1)).thenReturn(Arrays.asList(person));

        List<PersonEntity> result = personService.getPersonsActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personRepository, times(1)).findByActive(1);
    }

    @Test
    void findByIdentification_ShouldReturnPerson() {
        when(personRepository.findByIdentification("123456789", 1)).thenReturn(person);

        PersonEntity result = personService.findByIdentification("123456789");

        assertNotNull(result);
        assertEquals("123456789", result.getIdentification());
        verify(personRepository, times(1)).findByIdentification("123456789", 1);
    }

    @Test
    void findByEmail_ShouldReturnPerson() {
        when(personRepository.findByEmail("john@example.com", 1)).thenReturn(person);

        PersonEntity result = personService.findByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(personRepository, times(1)).findByEmail("john@example.com", 1);
    }

    @Test
    void savePerson_ShouldReturnSavedPerson() {
        when(personRepository.save(any(PersonEntity.class))).thenReturn(person);

        PersonEntity result = personService.savePerson(person);

        assertNotNull(result);
        assertEquals(person.getId(), result.getId());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    void getPersonById_WhenExists_ShouldReturnPerson() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        PersonEntity result = personService.getPersonById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(personRepository, times(1)).findById(1L);
    }

    @Test
    void getPersonById_WhenDoesNotExist_ShouldReturnNull() {
        when(personRepository.findById(2L)).thenReturn(Optional.empty());

        PersonEntity result = personService.getPersonById(2L);

        assertNull(result);
        verify(personRepository, times(1)).findById(2L);
    }

    @Test
    void updatePerson_ShouldReturnUpdatedPerson() {
        when(personRepository.save(any(PersonEntity.class))).thenReturn(person);

        PersonEntity result = personService.updatePerson(person);

        assertNotNull(result);
        assertEquals(person.getId(), result.getId());
        verify(personRepository, times(1)).save(person);
    }
}

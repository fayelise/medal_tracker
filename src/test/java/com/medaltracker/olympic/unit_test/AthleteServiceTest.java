package com.medaltracker.olympic.unit_test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medaltracker.olympic.dto.AthleteDTO;
import com.medaltracker.olympic.entity.Athlete;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.PaysRepository;
import com.medaltracker.olympic.service.AthleteService;

@ExtendWith(MockitoExtension.class)
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private PaysRepository paysRepository;

    @InjectMocks
    private AthleteService athleteService;

    private Athlete athlete;
    private Pays pays;

    @BeforeEach
    void setUp() {
        pays = new Pays();
        pays.setId(1L);
        pays.setNom("Senegal");

        athlete = new Athlete();
        athlete.setId(1L);
        athlete.setNom("Faye");
        athlete.setPrenom("Elise");
        athlete.setDiscipline("Athletics");
        athlete.setDateNaissance(LocalDate.of(2003, 7, 31));
        athlete.setPays(pays);
    }

    @Test
    void shouldReturnAllAthletes() {
        when(athleteRepository.findAll()).thenReturn(List.of(athlete));

        List<Athlete> result = athleteService.getAll();

        assertEquals(1, result.size());
        verify(athleteRepository).findAll();
    }

    @Test
    void shouldReturnAthleteById() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));

        Athlete result = athleteService.getById(1L);

        assertNotNull(result);
        assertEquals("Faye", result.getNom());
    }

    @Test
    void shouldThrowExceptionWhenAthleteNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            athleteService.getById(1L);
        });
    }

    @Test
    void shouldReturnAthletesByPays() {
        when(athleteRepository.findByPaysId(1L)).thenReturn(List.of(athlete));

        List<Athlete> result = athleteService.getByPays(1L);

        assertEquals(1, result.size());
        verify(athleteRepository).findByPaysId(1L);
    }

    @Test
    void shouldCreateAthlete() {
        AthleteDTO dto = new AthleteDTO(
                athlete.getNom(),
                athlete.getPrenom(),
                athlete.getDiscipline(),
                athlete.getDateNaissance(),
                pays.getId()
        );

        when(paysRepository.findById(1L)).thenReturn(Optional.of(pays));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athlete);

        Athlete result = athleteService.create(dto);

        assertNotNull(result);
        assertEquals("Senegal", result.getPays().getNom());
        verify(athleteRepository).save(any(Athlete.class));
    }

    @Test
    void shouldThrowExceptionWhenPaysNotFoundOnCreate() {
        AthleteDTO dto = new AthleteDTO(
                athlete.getNom(),
                athlete.getPrenom(),
                athlete.getDiscipline(),
                athlete.getDateNaissance(),
                pays.getId()
        );

        when(paysRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            athleteService.create(dto);
        });
    }

    @Test
    void shouldUpdateAthlete() {
        AthleteDTO dto = new AthleteDTO(
                "James",
                "Shaniqua",
                "Judo",
                LocalDate.of(1998, 5, 10),
                null
        );

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(athlete);

        Athlete result = athleteService.update(1L, dto);

        assertEquals("James", result.getNom());
        assertEquals("Shaniqua", result.getPrenom());
        assertEquals("Judo", result.getDiscipline());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAthlete() {
        AthleteDTO dto = new AthleteDTO();

        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            athleteService.update(1L, dto);
        });
    }

    @Test
    void shouldDeleteAthlete() {
        
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        doNothing().when(athleteRepository).delete(athlete);

        athleteService.delete(1L);

        verify(athleteRepository).findById(1L);  
        verify(athleteRepository).delete(athlete);
    }

}

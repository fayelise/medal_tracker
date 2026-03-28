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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.medaltracker.olympic.entity.Athlete;
import com.medaltracker.olympic.entity.Competition;
import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.entity.enums.StatutCompetition;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.CompetitionRepository;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.service.MedailleService;

@ExtendWith(MockitoExtension.class)
class MedailleServiceTest {

    @Mock
    private MedailleRepository medailleRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private MedailleService medailleService;

    private Athlete athlete;
    private Competition competition;
    private Pays pays;

    @BeforeEach
    void setUp() {
        pays = new Pays();
        pays.setId(1L);
        pays.setNom("Senegal");

        athlete = new Athlete();
        athlete.setId(1L);
        athlete.setPays(pays);

        competition = new Competition();
        competition.setId(1L);
        competition.setStatut(StatutCompetition.TERMINEE);
    }

    @Test
    void shouldReturnAllMedals() {
        when(medailleRepository.findAll()).thenReturn(List.of(new Medaille()));

        List<Medaille> result = medailleService.getAll();

        assertEquals(1, result.size());
        verify(medailleRepository).findAll();
    }

    @Test
    void shouldReturnMedalById() {
        Medaille medaille = new Medaille();

        when(medailleRepository.findById(1L)).thenReturn(Optional.of(medaille));

        Medaille result = medailleService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowExceptionWhenMedalNotFound() {
        when(medailleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            medailleService.getById(1L);
        });
    }

    @Test
    void shouldAssignMedalSuccessfully() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(medailleRepository.save(any(Medaille.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Medaille result = medailleService.attribuerMedaille(
                1L, 1L, TypeMedaille.OR, LocalDate.now());

        assertNotNull(result);
        assertEquals(TypeMedaille.OR, result.getType());
        assertEquals(athlete, result.getAthlete());
        assertEquals(pays, result.getPays());
        assertEquals(competition, result.getCompetition());
        assertEquals(LocalDate.now(), result.getDateObtention());
    }

    @Test
    void shouldThrowExceptionWhenAthleteNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            medailleService.attribuerMedaille(1L, 1L, TypeMedaille.OR, LocalDate.now());
        });
    }

    @Test
    void shouldThrowExceptionWhenCompetitionNotFound() {
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            medailleService.attribuerMedaille(1L, 1L, TypeMedaille.OR, LocalDate.now());
        });
    }

    @Test
    void shouldThrowExceptionWhenCompetitionNotFinished() {
        competition.setStatut(StatutCompetition.EN_COURS);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(athlete));
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        assertThrows(RuntimeException.class, () -> {
            medailleService.attribuerMedaille(1L, 1L, TypeMedaille.OR, LocalDate.now());
        });
    }

    @Test
    void shouldReturnMedalsByAthlete() {
        when(medailleRepository.findByAthleteId(1L))
                .thenReturn(List.of(new Medaille()));

        List<Medaille> result = medailleService.getByAthlete(1L);

        assertEquals(1, result.size());
        verify(medailleRepository).findByAthleteId(1L);
    }

    @Test
    void shouldReturnMedalsByCompetition() {
        when(medailleRepository.findByCompetitionId(1L))
                .thenReturn(List.of(new Medaille()));

        List<Medaille> result = medailleService.getByCompetition(1L);

        assertEquals(1, result.size());
        verify(medailleRepository).findByCompetitionId(1L);
    }
}

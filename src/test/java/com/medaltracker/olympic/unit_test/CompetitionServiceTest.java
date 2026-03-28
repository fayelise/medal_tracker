package com.medaltracker.olympic.unit_test;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medaltracker.olympic.dto.CompetitionDTO;
import com.medaltracker.olympic.entity.Competition;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.CompetitionRepository;
import com.medaltracker.olympic.service.CompetitionService;
import com.medaltracker.olympic.entity.enums.StatutCompetition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @InjectMocks
    private CompetitionService competitionService;

    private Competition competition;
    

    @BeforeEach
    void setUp() {
        competition = new Competition();
        competition.setId(1L);
        competition.setNom("Jeux Olympiques");
        competition.setDiscipline("Athletics");
        competition.setDateDebut(LocalDate.of(2024, 7, 26));
        competition.setDateFin(LocalDate.of(2024, 8, 11));
        competition.setStatut(StatutCompetition.EN_COURS); 
    }

    // =========================
    // getAll
    // =========================
    @Test
    void shouldReturnAllCompetitions() {
        when(competitionRepository.findAll()).thenReturn(List.of(competition));

        List<Competition> result = competitionService.getAll();

        assertEquals(1, result.size());
        verify(competitionRepository).findAll();
    }

    // =========================
    // getById - SUCCESS
    // =========================
    @Test
    void shouldReturnCompetitionById() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        Competition result = competitionService.getById(1L);

        assertNotNull(result);
        assertEquals("Jeux Olympiques", result.getNom());
    }

    // =========================
    // getById - NOT FOUND
    // =========================
    @Test
    void shouldThrowExceptionWhenCompetitionNotFound() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            competitionService.getById(1L);
        });
    }

    // =========================
    // create
    // =========================
    @Test
    void shouldCreateCompetition() {
        CompetitionDTO dto = new CompetitionDTO(
            "Jeux Olympiques", "Athletics", 
            LocalDate.of(2024, 7, 26), LocalDate.of(2024, 8, 11), 
            StatutCompetition.EN_COURS
        );
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        Competition result = competitionService.create(dto);

        assertNotNull(result);
        assertEquals("Jeux Olympiques", result.getNom());
        verify(competitionRepository).save(any(Competition.class));
    }

    // =========================
    // update
    // =========================
    @Test
    void shouldUpdateCompetition() {
        CompetitionDTO dto = new CompetitionDTO(
            "Updated Comp", "Athletics", 
            LocalDate.of(2024, 7, 26), LocalDate.of(2024, 8, 11), 
            StatutCompetition.TERMINEE
        );
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        Competition result = competitionService.update(1L, dto);

        assertNotNull(result);
        verify(competitionRepository).save(any(Competition.class));
    }

    // =========================
    // delete
    // =========================
    @Test
    void shouldDeleteCompetition() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        doNothing().when(competitionRepository).delete(competition);

        competitionService.delete(1L);

        verify(competitionRepository).findById(1L);
        verify(competitionRepository).delete(competition);
    }
}

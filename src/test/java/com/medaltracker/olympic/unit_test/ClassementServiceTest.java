package com.medaltracker.olympic.unit_test;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.medaltracker.olympic.dto.ClassementDTO;
import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;
import com.medaltracker.olympic.service.ClassementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassementServiceTest {

    @Mock
    private PaysRepository paysRepository;

    @Mock
    private MedailleRepository medailleRepository;

    @InjectMocks
    private ClassementService classementService;

    private Pays senegal;
    private Pays france;

    @BeforeEach
    void setUp() {
        senegal = new Pays();
        senegal.setId(1L);
        senegal.setNom("Senegal");

        france = new Pays();
        france.setId(2L);
        france.setNom("France");
    }

    // =========================
    // Classement par POINTS
    // =========================
    @Test
    void shouldRankCountriesByPoints() {

        // Senegal: 1 OR, 1 ARGENT → 3 + 2 = 5 pts
        List<Medaille> senegalMedals = List.of(
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.ARGENT)
        );

        // France: 1 OR → 3 pts
        List<Medaille> franceMedals = List.of(
                createMedal(TypeMedaille.OR)
        );

        when(paysRepository.findAll()).thenReturn(List.of(senegal, france));
        when(medailleRepository.findByPaysId(1L)).thenReturn(senegalMedals);
        when(medailleRepository.findByPaysId(2L)).thenReturn(franceMedals);

        List<ClassementDTO> result = classementService.getClassementParPoints();

        // Senegal should be first (5 pts > 3 pts)
        assertEquals("Senegal", result.get(0).getPays());
        assertEquals(5, result.get(0).getPoints());

        assertEquals("France", result.get(1).getPays());
        assertEquals(3, result.get(1).getPoints());
    }

    // =========================
    // Verify calculation
    // =========================
    @Test
    void shouldCalculateCorrectMedalCountsAndPoints() {

        List<Medaille> medals = List.of(
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.ARGENT),
                createMedal(TypeMedaille.BRONZE)
        );

        when(paysRepository.findAll()).thenReturn(List.of(senegal));
        when(medailleRepository.findByPaysId(1L)).thenReturn(medals);

        List<ClassementDTO> result = classementService.getClassementParPoints();

        ClassementDTO dto = result.get(0);

        assertEquals(2, dto.getOrCount());
        assertEquals(1, dto.getArgentCount());
        assertEquals(1, dto.getBronzeCount());
        assertEquals(4, dto.getTotal());
        assertEquals( (2*3) + (1*2) + 1, dto.getPoints()); 
    }

    // =========================
    // Classement par OR (tie-breakers)
    // =========================
    @Test
    void shouldRankByGoldThenSilverThenBronze() {

        // Senegal: 1 OR, 2 ARGENT
        List<Medaille> senegalMedals = List.of(
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.ARGENT),
                createMedal(TypeMedaille.ARGENT)
        );

        // France: 1 OR, 1 ARGENT, 1 BRONZE → less silver → should be below
        List<Medaille> franceMedals = List.of(
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.ARGENT),
                createMedal(TypeMedaille.BRONZE)
        );

        when(paysRepository.findAll()).thenReturn(List.of(senegal, france));
        when(medailleRepository.findByPaysId(1L)).thenReturn(senegalMedals);
        when(medailleRepository.findByPaysId(2L)).thenReturn(franceMedals);

        List<ClassementDTO> result = classementService.getClassementParMedaillesOr();

        // Same gold → compare silver → Senegal wins
        assertEquals("Senegal", result.get(0).getPays());
        assertEquals("France", result.get(1).getPays());
    }

    // =========================
    // Helper method
    // =========================
    private Medaille createMedal(TypeMedaille type) {
        Medaille m = new Medaille();
        m.setType(type);
        return m;
    }
}

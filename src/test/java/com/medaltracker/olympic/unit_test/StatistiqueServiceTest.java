package com.medaltracker.olympic.unit_test;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.service.StatistiqueService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatistiqueServiceTest {

    @Mock
    private MedailleRepository medailleRepository;

    @InjectMocks
    private StatistiqueService statistiqueService;

    private Long paysId;

    @BeforeEach
    void setUp() {
        paysId = 1L;
    }

    // =========================
    // Normal case
    // =========================
    @Test
    void shouldReturnCorrectStatisticsForCountry() {

        List<Medaille> medailles = List.of(
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.OR),
                createMedal(TypeMedaille.ARGENT),
                createMedal(TypeMedaille.BRONZE)
        );

        when(medailleRepository.findByPaysId(paysId)).thenReturn(medailles);

        Map<String, Integer> result = statistiqueService.statsParPays(paysId);

        assertEquals(2, result.get("OR"));
        assertEquals(1, result.get("ARGENT"));
        assertEquals(1, result.get("BRONZE"));
        assertEquals(4, result.get("TOTAL"));
    }

    // =========================
    // No medals case
    // =========================
    @Test
    void shouldReturnZeroStatisticsWhenNoMedals() {

        when(medailleRepository.findByPaysId(paysId)).thenReturn(List.of());

        Map<String, Integer> result = statistiqueService.statsParPays(paysId);

        assertEquals(0, result.get("OR"));
        assertEquals(0, result.get("ARGENT"));
        assertEquals(0, result.get("BRONZE"));
        assertEquals(0, result.get("TOTAL"));
    }

    // =========================
    //  Only one type of medal
    // =========================
    @Test
    void shouldHandleSingleMedalType() {

        List<Medaille> medailles = List.of(
                createMedal(TypeMedaille.BRONZE),
                createMedal(TypeMedaille.BRONZE)
        );

        when(medailleRepository.findByPaysId(paysId)).thenReturn(medailles);

        Map<String, Integer> result = statistiqueService.statsParPays(paysId);

        assertEquals(0, result.get("OR"));
        assertEquals(0, result.get("ARGENT"));
        assertEquals(2, result.get("BRONZE"));
        assertEquals(2, result.get("TOTAL"));
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

package com.medaltracker.olympic.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.entity.enums.TypeMedaille;  
import com.medaltracker.olympic.repository.MedailleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatistiqueService {

    private final MedailleRepository medailleRepository;

    public Map<String, Integer> statsParPays(Long paysId) {
        log.debug("Calcul des statistiques pour le pays ID {}", paysId);
        List<Medaille> medailles = medailleRepository.findByPaysId(paysId);
        return calculerStats(medailles);
    }

    

    private Map<String, Integer> calculerStats(List<Medaille> medailles) {
        int or = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.OR).count();
        int argent = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.ARGENT).count();
        int bronze = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.BRONZE).count();

        Map<String, Integer> stats = new HashMap<>();
        stats.put("OR", or);
        stats.put("ARGENT", argent);
        stats.put("BRONZE", bronze);
        stats.put("TOTAL", or + argent + bronze);
        stats.put("POINTS", (or * 3) + (argent * 2) + bronze);

        return stats;
    }
}

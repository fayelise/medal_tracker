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

/**
 * Service gérant le calcul des statistiques de médailles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatistiqueService {

    private final MedailleRepository medailleRepository;

    /**
     * Calcule les statistiques globales (Or, Argent, Bronze, Total, Points) pour un pays.
     * @param paysId Identifiant du pays
     * @return Map contenant les statistiques calculées
     */
    public Map<String, Integer> statsParPays(Long paysId) {
        log.debug("Calcul des statistiques pour le pays ID {}", paysId);
        List<Medaille> medailles = medailleRepository.findByPaysId(paysId);
        return calculerStats(medailles);
    }

    /**
     * Méthode utilitaire pour calculer les compteurs à partir d'une liste de médailles.
     * @param medailles Liste de médailles
     * @return Map des statistiques
     */
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

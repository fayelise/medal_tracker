package com.medaltracker.olympic.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.medaltracker.olympic.dto.ClassementDTO;
import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import com.medaltracker.olympic.repository.MedailleRepository;
import com.medaltracker.olympic.repository.PaysRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gérant le calcul des classements des médailles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClassementService {

    private final PaysRepository paysRepository;
    private final MedailleRepository medailleRepository;

    /**
     * Génère le classement par nombre total de médailles.
     * @return Liste de ClassementDTO triée par total décroissant
     */
    public List<ClassementDTO> getClassementParTotal() {
        log.info("Génération du classement par nombre total de médailles");
        List<Pays> paysList = paysRepository.findAll();
        return paysList.stream().map(this::mapToClassementDTO)
                .sorted(Comparator.comparingInt(ClassementDTO::getTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Génère le classement par système de points (Or=3, Argent=2, Bronze=1).
     * @return Liste de ClassementDTO triée par points décroissants
     */
    public List<ClassementDTO> getClassementParPoints() {
        log.info("Génération du classement par système de points (3-2-1)");
        List<Pays> paysList = paysRepository.findAll();
        return paysList.stream().map(this::mapToClassementDTO)
                .sorted(Comparator.comparingInt(ClassementDTO::getPoints).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Génère le classement officiel par médailles d'or (puis argent, puis bronze).
     * @return Liste de ClassementDTO triée selon les règles olympiques
     */
    public List<ClassementDTO> getClassementParMedaillesOr() {
        log.info("Génération du classement par médailles d'or");
        List<Pays> paysList = paysRepository.findAll();
        return paysList.stream().map(this::mapToClassementDTO)
                .sorted((a, b) -> {
                    int compareOr = Integer.compare(b.getOrCount(), a.getOrCount());
                    if (compareOr != 0) return compareOr;
                    int compareArgent = Integer.compare(b.getArgentCount(), a.getArgentCount());
                    if (compareArgent != 0) return compareArgent;
                    return Integer.compare(b.getBronzeCount(), a.getBronzeCount());
                }).collect(Collectors.toList());
    }

    /**
     * Convertit une entité Pays en ClassementDTO en calculant ses médailles.
     * @param p Entité Pays
     * @return DTO de classement calculé
     */
    private ClassementDTO mapToClassementDTO(Pays p) {
        List<Medaille> medailles = medailleRepository.findByPaysId(p.getId());
        int or = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.OR).count();
        int argent = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.ARGENT).count();
        int bronze = (int) medailles.stream().filter(m -> m.getType() == TypeMedaille.BRONZE).count();
        int total = or + argent + bronze;
        int points = (or * 3) + (argent * 2) + bronze;

        return new ClassementDTO(p.getNom(), or, argent, bronze, total, points);
    }
}
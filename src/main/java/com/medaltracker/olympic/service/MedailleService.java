package com.medaltracker.olympic.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedailleService {

    private final MedailleRepository medailleRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;

    public List<Medaille> getAll() {
        return medailleRepository.findAll();
    }

    public Medaille getById(Long id) {
        return medailleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médaille introuvable"));
    }

    public Medaille attribuerMedaille(Long athleteId, Long competitionId, TypeMedaille type, LocalDate dateObtention) {

        log.info("Attribution d'une médaille de type {} à l'athlète ID {} pour la compétition ID {}", type, athleteId, competitionId);

        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlète introuvable"));

        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Compétition introuvable"));

        if (competition.getStatut() != StatutCompetition.TERMINEE) {
            throw new RuntimeException("La compétition doit être terminée");
        }

        // Vérification de la discipline
        if (!athlete.getDiscipline().trim().equalsIgnoreCase(competition.getDiscipline().trim())) {
            log.error("Incohérence de discipline : l'athlète {} (discipline: {}) ne peut pas gagner une médaille en {}", 
                athlete.getNom(), athlete.getDiscipline(), competition.getDiscipline());
            throw new RuntimeException("L'athlète ne peut gagner une médaille que dans sa propre discipline : " + athlete.getDiscipline());
        }

        Pays pays = athlete.getPays();

        LocalDate dateMedaille = dateObtention != null ? dateObtention : LocalDate.now();

        // Vérification de la date de médaille
        if (dateMedaille.isBefore(competition.getDateFin())) {
            log.error("Échec d'attribution de la médaille : la date d'obtention ({}) est avant la date de fin de la compétition ({})", 
                dateMedaille, competition.getDateFin());
            throw new RuntimeException("La date d'obtention de la médaille doit être supérieure ou égale à la date de fin de la compétition (" + competition.getDateFin() + ")");
        }

        Medaille medaille = new Medaille();
        medaille.setAthlete(athlete);
        medaille.setPays(pays);
        medaille.setCompetition(competition);
        medaille.setType(type);
        medaille.setDateObtention(dateMedaille);

        Medaille saved = medailleRepository.save(medaille);
        log.info("Médaille ID {} enregistrée avec succès pour le pays {}", saved.getId(), pays.getNom());
        return saved;
    }

    public List<Medaille> getByAthlete(Long athleteId) {
        return medailleRepository.findByAthleteId(athleteId);
    }

    public List<Medaille> getByCompetition(Long competitionId) {
        return medailleRepository.findByCompetitionId(competitionId);
    }
}
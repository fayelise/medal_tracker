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

/**
 * Service gérant l'attribution et la consultation des médailles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedailleService {

    private final MedailleRepository medailleRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;

    /**
     * Récupère la liste de toutes les médailles attribuées.
     * @return Liste de médailles
     */
    public List<Medaille> getAll() {
        return medailleRepository.findAll();
    }

    /**
     * Récupère une médaille par son identifiant.
     * @param id Identifiant de la médaille
     * @return La médaille trouvée
     * @throws ResourceNotFoundException si la médaille n'existe pas
     */
    public Medaille getById(Long id) {
        return medailleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médaille introuvable"));
    }

    /**
     * Attribue une médaille à un athlète pour une compétition donnée.
     * Vérifie la cohérence des disciplines et des dates.
     * @param athleteId Identifiant de l'athlète
     * @param competitionId Identifiant de la compétition
     * @param type Type de médaille (OR, ARGENT, BRONZE)
     * @param dateObtention Date d'obtention
     * @return La médaille enregistrée
     * @throws ResourceNotFoundException si l'athlète ou la compétition n'existe pas
     * @throws RuntimeException si la compétition n'est pas terminée, si les disciplines ne correspondent pas, 
     *                          ou si la date d'obtention est invalide
     */
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

    /**
     * Récupère les médailles obtenues par un athlète.
     * @param athleteId Identifiant de l'athlète
     * @return Liste de médailles
     */
    public List<Medaille> getByAthlete(Long athleteId) {
        return medailleRepository.findByAthleteId(athleteId);
    }

    /**
     * Récupère les médailles attribuées pour une compétition.
     * @param competitionId Identifiant de la compétition
     * @return Liste de médailles
     */
    public List<Medaille> getByCompetition(Long competitionId) {
        return medailleRepository.findByCompetitionId(competitionId);
    }
}
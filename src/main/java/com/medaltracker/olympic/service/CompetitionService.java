package com.medaltracker.olympic.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.medaltracker.olympic.dto.CompetitionDTO;
import com.medaltracker.olympic.entity.Competition;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.CompetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gérant la logique métier pour les compétitions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    /**
     * Récupère la liste de toutes les compétitions.
     * @return Liste de compétitions
     */
    public List<Competition> getAll() {
        return competitionRepository.findAll();
    }

    /**
     * Récupère une page de compétitions.
     * @param pageable Informations de pagination
     * @return Page de compétitions
     */
    public Page<Competition> getAll(Pageable pageable) {
        log.debug("Récupération de toutes les compétitions (page {})", pageable.getPageNumber());
        return competitionRepository.findAll(pageable);
    }

    /**
     * Récupère une compétition par son identifiant.
     * @param id Identifiant de la compétition
     * @return La compétition trouvée
     * @throws ResourceNotFoundException si la compétition n'existe pas
     */
    public Competition getById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentative de récupération d'une compétition inexistante avec l'ID {}", id);
                    return new ResourceNotFoundException("Compétition introuvable");
                });
    }

    /**
     * Crée une nouvelle compétition avec validation des dates.
     * @param dto Données de la compétition
     * @return La compétition créée
     * @throws RuntimeException si la date de fin est avant la date de début
     */
    public Competition create(CompetitionDTO dto) {
        log.info("Création d'une nouvelle compétition: {}", dto.getNom());
        
        // Validation des dates
        if (dto.getDateFin().isBefore(dto.getDateDebut())) {
            log.error("Échec de création de la compétition : la date de fin ({}) est avant la date de début ({})", 
                dto.getDateFin(), dto.getDateDebut());
            throw new RuntimeException("La date de fin de la compétition doit être après la date de début");
        }

        Competition comp = new Competition();
        comp.setNom(dto.getNom());
        comp.setDiscipline(dto.getDiscipline());
        comp.setDateDebut(dto.getDateDebut());
        comp.setDateFin(dto.getDateFin());
        comp.setStatut(dto.getStatut());
        Competition saved = competitionRepository.save(comp);
        log.info("Compétition '{}' créée avec succès avec l'ID {}", saved.getNom(), saved.getId());
        return saved;
    }

    /**
     * Met à jour les informations d'une compétition avec validation des dates.
     * @param id Identifiant de la compétition
     * @param dto Nouvelles données
     * @return La compétition mise à jour
     * @throws ResourceNotFoundException si la compétition n'existe pas
     * @throws RuntimeException si la cohérence des dates n'est pas respectée
     */
    public Competition update(Long id, CompetitionDTO dto) {
        log.info("Mise à jour de la compétition ID {}", id);
        Competition comp = getById(id);

        if (dto.getNom() != null) comp.setNom(dto.getNom());
        if (dto.getDiscipline() != null) comp.setDiscipline(dto.getDiscipline());
        
        // Validation des dates lors de la mise à jour
        if (dto.getDateDebut() != null) comp.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) comp.setDateFin(dto.getDateFin());
        
        if (comp.getDateFin().isBefore(comp.getDateDebut())) {
            log.error("Échec de mise à jour de la compétition ID {} : la date de fin ({}) est avant la date de début ({})", 
                id, comp.getDateFin(), comp.getDateDebut());
            throw new RuntimeException("La date de fin de la compétition doit être après la date de début");
        }

        if (dto.getStatut() != null) comp.setStatut(dto.getStatut());

        Competition updated = competitionRepository.save(comp);
        log.info("Compétition ID {} mise à jour avec succès", updated.getId());
        return updated;
    }

    /**
     * Supprime une compétition.
     * @param id Identifiant de la compétition
     * @throws ResourceNotFoundException si la compétition n'existe pas
     */
    public void delete(Long id) {
        log.info("Suppression de la compétition ID {}", id);
        Competition comp = getById(id);
        competitionRepository.delete(comp);
        log.info("Compétition ID {} supprimée avec succès", id);
    }
}

package com.medaltracker.olympic.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.medaltracker.olympic.dto.AthleteDTO;
import com.medaltracker.olympic.entity.Athlete;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.AthleteRepository;
import com.medaltracker.olympic.repository.PaysRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gérant la logique métier pour les athlètes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final PaysRepository paysRepository;

    /**
     * Récupère la liste de tous les athlètes.
     * @return Liste d'athlètes
     */
    public List<Athlete> getAll() {
        return athleteRepository.findAll();
    }

    /**
     * Récupère une page d'athlètes.
     * @param pageable Informations de pagination
     * @return Page d'athlètes
     */
    public Page<Athlete> getAll(Pageable pageable) {
        log.debug("Récupération de tous les athlètes (page {})", pageable.getPageNumber());
        return athleteRepository.findAll(pageable);
    }

    /**
     * Récupère un athlète par son identifiant.
     * @param id Identifiant de l'athlète
     * @return L'athlète trouvé
     * @throws ResourceNotFoundException si l'athlète n'existe pas
     */
    public Athlete getById(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentative de récupération d'un athlète inexistant avec l'ID {}", id);
                    return new ResourceNotFoundException("Athlète introuvable");
                });
    }

    /**
     * Récupère la liste des athlètes d'un pays.
     * @param paysId Identifiant du pays
     * @return Liste d'athlètes
     */
    public List<Athlete> getByPays(Long paysId) {
        return athleteRepository.findByPaysId(paysId);
    }

    /**
     * Récupère une page d'athlètes d'un pays.
     * @param paysId Identifiant du pays
     * @param pageable Informations de pagination
     * @return Page d'athlètes
     */
    public Page<Athlete> getByPays(Long paysId, Pageable pageable) {
        log.debug("Récupération des athlètes du pays ID {} (page {})", paysId, pageable.getPageNumber());
        return athleteRepository.findByPaysId(paysId, pageable);
    }

    /**
     * Crée un nouvel athlète.
     * @param dto Données de l'athlète
     * @return L'athlète créé
     * @throws ResourceNotFoundException si le pays n'existe pas
     */
    public Athlete create(AthleteDTO dto) {

    log.info("Création d'un nouvel athlète: {} {}", dto.getPrenom(), dto.getNom());

    Pays pays = paysRepository.findById(dto.getPaysId())
            .orElseThrow(() -> {
                log.error("Échec de création de l'athlète : pays ID {} introuvable", dto.getPaysId());
                return new ResourceNotFoundException("Pays introuvable");
            });

        Athlete athlete = new Athlete();
        athlete.setNom(dto.getNom());
        athlete.setPrenom(dto.getPrenom());
        athlete.setDiscipline(dto.getDiscipline());
        athlete.setDateNaissance(dto.getDateNaissance());
        athlete.setPays(pays);

        Athlete saved = athleteRepository.save(athlete);
        log.info("Athlète ID {} créé avec succès", saved.getId());
        return saved;
    }

    /**
     * Met à jour les informations d'un athlète.
     * @param id Identifiant de l'athlète
     * @param dto Nouvelles données
     * @return L'athlète mis à jour
     * @throws ResourceNotFoundException si l'athlète ou le nouveau pays n'existe pas
     */
    public Athlete update(Long id, AthleteDTO dto) {
        log.info("Mise à jour de l'athlète ID {}", id);
        Athlete athlete = getById(id);

        if (dto.getNom() != null) athlete.setNom(dto.getNom());
        if (dto.getPrenom() != null) athlete.setPrenom(dto.getPrenom());
        if (dto.getDiscipline() != null) athlete.setDiscipline(dto.getDiscipline());
        if (dto.getDateNaissance() != null) athlete.setDateNaissance(dto.getDateNaissance());
        
        if (dto.getPaysId() != null) {
            Pays pays = paysRepository.findById(dto.getPaysId())
                    .orElseThrow(() -> {
                        log.error("Échec de mise à jour de l'athlète : pays ID {} introuvable", dto.getPaysId());
                        return new ResourceNotFoundException("Pays introuvable");
                    });
            athlete.setPays(pays);
        }

        Athlete updated = athleteRepository.save(athlete);
        log.info("Athlète ID {} mis à jour avec succès", updated.getId());
        return updated;
    }

    /**
     * Supprime un athlète.
     * @param id Identifiant de l'athlète
     * @throws ResourceNotFoundException si l'athlète n'existe pas
     */
    public void delete(Long id) {
        log.info("Suppression de l'athlète ID {}", id);
        Athlete athlete = getById(id);
        athleteRepository.delete(athlete);
        log.info("Athlète ID {} supprimé avec succès", id);
    }

    
}

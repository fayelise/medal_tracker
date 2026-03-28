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

@Service
@RequiredArgsConstructor
@Slf4j
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final PaysRepository paysRepository;

    public List<Athlete> getAll() {
        return athleteRepository.findAll();
    }

    public Page<Athlete> getAll(Pageable pageable) {
        log.debug("Récupération de tous les athlètes (page {})", pageable.getPageNumber());
        return athleteRepository.findAll(pageable);
    }

    public Athlete getById(Long id) {
        return athleteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentative de récupération d'un athlète inexistant avec l'ID {}", id);
                    return new ResourceNotFoundException("Athlète introuvable");
                });
    }

    public List<Athlete> getByPays(Long paysId) {
        return athleteRepository.findByPaysId(paysId);
    }

    public Page<Athlete> getByPays(Long paysId, Pageable pageable) {
        log.debug("Récupération des athlètes du pays ID {} (page {})", paysId, pageable.getPageNumber());
        return athleteRepository.findByPaysId(paysId, pageable);
    }

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

    public void delete(Long id) {
        log.info("Suppression de l'athlète ID {}", id);
        Athlete athlete = getById(id);
        athleteRepository.delete(athlete);
        log.info("Athlète ID {} supprimé avec succès", id);
    }

    
}

package com.medaltracker.olympic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.medaltracker.olympic.dto.PaysDTO;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.exception.ResourceNotFoundException;
import com.medaltracker.olympic.repository.PaysRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service gérant la logique métier pour les pays.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaysService {

    private final PaysRepository paysRepository;

    /**
     * Récupère la liste de tous les pays.
     * @return Liste de pays
     */
    public List<Pays> getAll() {
        return paysRepository.findAll();
    }

    /**
     * Récupère une page de pays.
     * @param pageable Informations de pagination
     * @return Page de pays
     */
    public Page<Pays> getAll(Pageable pageable) {
        log.debug("Récupération de tous les pays (page {})", pageable.getPageNumber());
        return paysRepository.findAll(pageable);
    }

    /**
     * Récupère un pays par son identifiant.
     * @param id Identifiant du pays
     * @return Le pays trouvé
     * @throws ResourceNotFoundException si le pays n'existe pas
     */
    public Pays getById(Long id) {
        return paysRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tentative de récupération d'un pays inexistant avec l'ID {}", id);
                    return new ResourceNotFoundException("Pays introuvable");
                });
    }

    /**
     * Crée un nouveau pays.
     * @param dto Données du pays
     * @return Le pays créé
     */
    public Pays create(PaysDTO dto) {
        log.info("Création d'un nouveau pays: {}", dto.getNom());
        Pays pays = new Pays();
        pays.setNom(dto.getNom());
        pays.setCode(dto.getCode());
        pays.setDrapeau(dto.getDrapeau());
        Pays saved = paysRepository.save(pays);
        log.info("Pays '{}' créé avec succès avec l'ID {}", saved.getNom(), saved.getId());
        return saved;
    }

    /**
     * Crée plusieurs pays en une seule opération (batch).
     * @param paysList Liste de DTOs de pays
     * @return Liste des pays créés
     */
    public List<Pays> createAll(List<PaysDTO> paysList) {
        log.info("Création groupée de {} pays", paysList.size());
        List<Pays> entities = paysList.stream().map(dto -> {
            Pays p = new Pays();
            p.setNom(dto.getNom());
            p.setCode(dto.getCode());
            p.setDrapeau(dto.getDrapeau());
            return p;
        }).collect(Collectors.toList());
        List<Pays> saved = paysRepository.saveAll(entities);
        log.info("{} pays créés avec succès", saved.size());
        return saved;
    }

    /**
     * Met à jour les informations d'un pays.
     * @param id Identifiant du pays
     * @param dto Nouvelles données
     * @return Le pays mis à jour
     * @throws ResourceNotFoundException si le pays n'existe pas
     */
    public Pays update(Long id, PaysDTO dto) {
        log.info("Mise à jour du pays ID {}", id);
        Pays pays = getById(id);

        if (dto.getNom() != null) pays.setNom(dto.getNom());
        if (dto.getCode() != null) pays.setCode(dto.getCode());
        if (dto.getDrapeau() != null) pays.setDrapeau(dto.getDrapeau());

        Pays updated = paysRepository.save(pays);
        log.info("Pays ID {} mis à jour avec succès", updated.getId());
        return updated;
    }

    /**
     * Supprime un pays.
     * @param id Identifiant du pays
     * @throws ResourceNotFoundException si le pays n'existe pas
     */
    public void delete(Long id) {
      log.info("Suppression du pays ID {}", id);
      Pays pays = getById(id);
      paysRepository.delete(pays);
      log.info("Pays ID {} supprimé avec succès", id);
}
}

package com.medaltracker.olympic.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medaltracker.olympic.dto.AthleteDTO;
import com.medaltracker.olympic.entity.Athlete;
import com.medaltracker.olympic.service.AthleteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Contrôleur REST pour la gestion des athlètes.
 * Fournit des endpoints pour le CRUD et la recherche par pays.
 */
@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteService athleteService;
    

    /**
     * Récupère la liste paginée de tous les athlètes.
     * @param pageable informations de pagination
     * @return page d'athlètes
     */
    @GetMapping
    public ResponseEntity<Page<Athlete>> getAll(Pageable pageable) {
        return ResponseEntity.ok(athleteService.getAll(pageable));
    }

    /**
     * Récupère un athlète par son identifiant.
     * @param id identifiant de l'athlète
     * @return l'athlète trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getById(@PathVariable Long id) {
        return ResponseEntity.ok(athleteService.getById(id));
    }

    /**
     * Récupère la liste paginée des athlètes appartenant à un pays donné.
     * @param paysId identifiant du pays
     * @param pageable informations de pagination
     * @return page d'athlètes du pays
     */
    @GetMapping("/pays/{paysId}")
    public ResponseEntity<Page<Athlete>> getByPays(@PathVariable Long paysId, Pageable pageable) {
        return ResponseEntity.ok(athleteService.getByPays(paysId, pageable));
    }

    /**
     * Crée un nouvel athlète.
     * @param dto données de l'athlète à créer
     * @return l'athlète créé
     */
    @PostMapping
    public ResponseEntity<Athlete> create(@Valid @RequestBody AthleteDTO dto) {
        return new ResponseEntity<>(athleteService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Athlete> update(@PathVariable Long id, @Valid @RequestBody AthleteDTO dto) {
        return ResponseEntity.ok(athleteService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        athleteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.medaltracker.olympic.controller;

// import java.util.List;

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

import com.medaltracker.olympic.dto.CompetitionDTO;
import com.medaltracker.olympic.entity.Competition;
import com.medaltracker.olympic.service.CompetitionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ResponseEntity<Page<Competition>> getAll(Pageable pageable) {
        return ResponseEntity.ok(competitionService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Competition> getById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Competition> create(@Valid @RequestBody CompetitionDTO dto) {
        return new ResponseEntity<>(competitionService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Competition> update(@PathVariable Long id, @Valid @RequestBody CompetitionDTO updated) {
        return ResponseEntity.ok(competitionService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        competitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

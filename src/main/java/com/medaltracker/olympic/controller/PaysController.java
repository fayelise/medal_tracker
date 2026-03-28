package com.medaltracker.olympic.controller;

import java.util.List;

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

import com.medaltracker.olympic.dto.PaysDTO;
import com.medaltracker.olympic.entity.Pays;
import com.medaltracker.olympic.service.PaysService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pays")
@RequiredArgsConstructor
public class PaysController {

    private final PaysService paysService;

    @GetMapping
    public ResponseEntity<Page<Pays>> getAll(Pageable pageable) {
        return ResponseEntity.ok(paysService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pays> getById(@PathVariable Long id) {
        Pays pays = paysService.getById(id);
        return ResponseEntity.ok(pays);
    }

    @PostMapping
    public ResponseEntity<Pays> create(@Valid @RequestBody PaysDTO paysDTO) {
        return new ResponseEntity<>(paysService.create(paysDTO), HttpStatus.CREATED);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Pays>> createBatch(@Valid @RequestBody List<PaysDTO> paysList) {
        return new ResponseEntity<>(paysService.createAll(paysList), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pays> update(@PathVariable Long id, @Valid @RequestBody PaysDTO dto) {
        return ResponseEntity.ok(paysService.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        paysService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

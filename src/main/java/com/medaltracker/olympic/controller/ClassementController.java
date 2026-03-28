package com.medaltracker.olympic.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medaltracker.olympic.service.ClassementService;
import com.medaltracker.olympic.service.StatistiqueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/classement")
@RequiredArgsConstructor
public class ClassementController {

    private final ClassementService classementService;
    private final StatistiqueService statistiqueService;

    @GetMapping
    public ResponseEntity<?> getClassement(@RequestParam(required = false) String tri) {

        if ("points".equalsIgnoreCase(tri)) {
            return ResponseEntity.ok(classementService.getClassementParPoints());
        } else if ("or".equalsIgnoreCase(tri)) {
            return ResponseEntity.ok(classementService.getClassementParMedaillesOr());
        }

        // par défaut total 
        return ResponseEntity.ok(classementService.getClassementParTotal());
    }

    @GetMapping("/pays/{paysId}")
    public ResponseEntity<?> statsPays(@PathVariable Long paysId) {
        return ResponseEntity.ok(statistiqueService.statsParPays(paysId));
    }

}

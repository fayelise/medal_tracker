package com.medaltracker.olympic.controller;

import java.util.List;
import java.util.stream.Collectors;

// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medaltracker.olympic.dto.MedailleRequestDTO;
import com.medaltracker.olympic.dto.MedailleResponseDTO;
import com.medaltracker.olympic.entity.Medaille;
import com.medaltracker.olympic.service.MedailleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/medailles")
@RequiredArgsConstructor
public class MedailleController {

    
    private final MedailleService medailleService;

    @GetMapping
    public ResponseEntity<List<MedailleResponseDTO>> getAll() {
        List<MedailleResponseDTO> dtos = medailleService.getAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedailleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(convertToDTO(medailleService.getById(id)));
    }

    // Attribution de médaille
    @PostMapping
    public ResponseEntity<MedailleResponseDTO> attribuerMedaille(@Valid @RequestBody MedailleRequestDTO request) {
        Medaille medaille = medailleService.attribuerMedaille(
                request.getAthleteId(),
                request.getCompetitionId(),
                request.getType(),
                request.getDateObtention()
        );

        return ResponseEntity.status(201).body(convertToDTO(medaille));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<MedailleResponseDTO>> getByAthlete(@PathVariable Long athleteId) {
        List<MedailleResponseDTO> dtos = medailleService.getByAthlete(athleteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<MedailleResponseDTO>> getByCompetition(@PathVariable Long competitionId) {
        List<MedailleResponseDTO> dtos = medailleService.getByCompetition(competitionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private MedailleResponseDTO convertToDTO(Medaille medaille) {
        MedailleResponseDTO dto = new MedailleResponseDTO();
        dto.setId(medaille.getId());
        dto.setType(medaille.getType());
        dto.setDateObtention(medaille.getDateObtention());
        
        if (medaille.getAthlete() != null) {
            dto.setAthleteNom(medaille.getAthlete().getNom());
            dto.setAthletePrenom(medaille.getAthlete().getPrenom());
        }
        
        if (medaille.getPays() != null) {
            dto.setPaysNom(medaille.getPays().getNom());
        }
        
        if (medaille.getCompetition() != null) {
            dto.setCompetitionNom(medaille.getCompetition().getNom());
        }
        
        return dto;
    }
}

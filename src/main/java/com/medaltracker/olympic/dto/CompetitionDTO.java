package com.medaltracker.olympic.dto;

import java.time.LocalDate;
import com.medaltracker.olympic.entity.enums.StatutCompetition;

import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionDTO {
    
    private String nom;
    private String discipline;

    @PastOrPresent(message = "La date de debut ne peut pas être dans le futur")
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    private StatutCompetition statut;
}

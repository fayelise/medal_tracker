package com.medaltracker.olympic.dto;


import java.time.LocalDate;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AthleteDTO {
    
    private String nom;
    private String prenom;
    private String discipline;
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    private Long paysId;
}
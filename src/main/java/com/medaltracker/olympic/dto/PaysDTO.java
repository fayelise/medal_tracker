package com.medaltracker.olympic.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data; 
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaysDTO {
    
    private String nom;

    @Size(min = 2, max = 3, message = "Le code pays doit avoir 2 ou 3 caracteres")
    private String code;

    private String drapeau;
}

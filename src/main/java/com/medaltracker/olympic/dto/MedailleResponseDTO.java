package com.medaltracker.olympic.dto;

import java.time.LocalDate;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedailleResponseDTO {
    private Long id;
    private TypeMedaille type;
    private LocalDate dateObtention;
    private String athleteNom;
    private String athletePrenom;
    private String paysNom;
    private String competitionNom;
}

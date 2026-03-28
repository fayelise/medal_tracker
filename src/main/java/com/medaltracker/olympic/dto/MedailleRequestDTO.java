package com.medaltracker.olympic.dto;

import java.time.LocalDate;
import com.medaltracker.olympic.entity.enums.TypeMedaille;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedailleRequestDTO {
   
    private Long athleteId;
    private Long competitionId;
    private TypeMedaille type;
    @PastOrPresent(message = "La date d'obtention ne peut pas être dans le futur")
    private LocalDate dateObtention;
}

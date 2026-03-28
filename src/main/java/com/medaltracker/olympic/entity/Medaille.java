package com.medaltracker.olympic.entity;

import java.time.LocalDate;
import com.medaltracker.olympic.entity.enums.TypeMedaille;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medaille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeMedaille type;

    private LocalDate dateObtention;

    //Relation avec Médailles(N:1)
    @ManyToOne
    @JoinColumn(name = "athlete_id")
    private Athlete athlete;

    //Relation avec Pays(N:1)
    @ManyToOne
    @JoinColumn(name = "pays_id")
    private Pays pays;
    
    //Relation avec Médailles(N:1)
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;
}

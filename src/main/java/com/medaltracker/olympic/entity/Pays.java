package com.medaltracker.olympic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    private String drapeau;

    // Relation avec Athletes (1:N)
    @JsonIgnore
    @OneToMany(mappedBy = "pays")
    private List<Athlete> athletes;


    // Relation avec Medailles (1:N)
    @JsonIgnore
    @OneToMany(mappedBy = "pays")
    private List<Medaille> medailles;
}

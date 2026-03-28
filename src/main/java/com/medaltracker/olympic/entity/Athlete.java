package com.medaltracker.olympic.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;

    private String discipline;

    // Relation avec Pays (N:1)
    @ManyToOne
    @JoinColumn(name = "pays_id")
    private Pays pays;

    // Relation avec Médailles(1:N)
    @JsonIgnore
    @OneToMany(mappedBy = "athlete")
    private List<Medaille> medailles;
}

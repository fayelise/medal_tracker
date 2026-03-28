package com.medaltracker.olympic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medaltracker.olympic.entity.Medaille;

public interface MedailleRepository extends JpaRepository<Medaille, Long> {

    List<Medaille> findByAthleteId(Long athleteId);

    List<Medaille> findByPaysId(Long paysId);

    List<Medaille> findByCompetitionId(Long competitionId);
}

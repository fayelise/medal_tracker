package com.medaltracker.olympic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medaltracker.olympic.entity.Competition;
public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
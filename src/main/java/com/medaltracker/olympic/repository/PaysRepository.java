package com.medaltracker.olympic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medaltracker.olympic.entity.Pays;

public interface PaysRepository extends JpaRepository<Pays, Long> {
}


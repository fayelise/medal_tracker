package com.medaltracker.olympic.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.medaltracker.olympic.entity.Athlete;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    List<Athlete> findByPaysId(Long paysId);
    Page<Athlete> findByPaysId(Long paysId, Pageable pageable);
}

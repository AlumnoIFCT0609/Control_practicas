package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.EvaluacionTutor;

@Repository
public interface EvaluacionTutorRepository extends JpaRepository<EvaluacionTutor, Long> {
}

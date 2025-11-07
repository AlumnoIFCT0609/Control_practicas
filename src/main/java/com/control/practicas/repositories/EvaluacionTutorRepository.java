package com.control.practicas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.EvaluacionTutor;

@Repository
public interface EvaluacionTutorRepository extends JpaRepository<EvaluacionTutor, Long> {
	
	
	List<EvaluacionTutor> findByTutorCursoId(Long tutorCursoId);
	List<EvaluacionTutor> findByTutorPracticasId(Long tutorPracticasId);
    Optional<EvaluacionTutor> findByTutorPracticas_Id(Long tutorPracticasId);


	
	
	
}

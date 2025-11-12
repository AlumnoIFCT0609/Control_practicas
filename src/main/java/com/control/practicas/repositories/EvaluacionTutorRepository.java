package com.control.practicas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.EvaluacionTutor;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;

@Repository
public interface EvaluacionTutorRepository extends JpaRepository<EvaluacionTutor, Long> {
    //List<EvaluacionTutor> findByTutorCurso_Id(Long tutorCursoId);
	//List<EvaluacionTutor> findByTutorPracticas_Id(Long tutorPracticasId);
	  
    // Buscar por ID del TutorCurso
    List<EvaluacionTutor> findByTutorCurso_Id(Long tutorCursoId);
    
    // Buscar por objeto TutorCurso completo
    List<EvaluacionTutor> findByTutorCurso(TutorCurso tutorCurso);
    
    // Buscar por ID del TutorPracticas
    List<EvaluacionTutor> findByTutorPracticas_Id(Long tutorPracticasId);
    
    // Buscar por objeto TutorPracticas completo
    List<EvaluacionTutor> findByTutorPracticas(TutorPracticas tutorPracticas);
}

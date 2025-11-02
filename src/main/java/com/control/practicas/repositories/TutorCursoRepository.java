package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import com.control.practicas.models.Curso;
import com.control.practicas.models.TutorCurso;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorCursoRepository extends JpaRepository<TutorCurso, Long> {
	Optional<TutorCurso> findById(Long id);
	//List<Curso> findByTutorCurso(TutorCurso tutorCurso);
    Optional<TutorCurso> findByDni(String dni);
    Optional<TutorCurso> findByEmail(String email);
    List<TutorCurso> findByActivoTrue();
    List<TutorCurso> findByActivoFalse();
    long countByActivoTrue();
    List<TutorCurso> findByEspecialidad(String especialidad);
    List<TutorCurso> findByNombreContainingIgnoreCase(String nombre);
    List<TutorCurso> findByApellidosContainingIgnoreCase(String apellidos);
    List<TutorCurso> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombre, String apellidos);
}

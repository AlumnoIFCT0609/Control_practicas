package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.TutorPracticas;
//import com.control.practicas.models.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findById(Long id);
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByEmpresa(Empresa empresa);
    List<Alumno> findByTutorPracticas_Id(Long tutorPracticasId); //filtro para dinamica de seleccion en base a otro select
    List<Alumno> findByCurso_Id(Long cursoId);
    List<Alumno> findByEmpresa_Id(Long empresaId);
    Optional<Alumno> findByDni(String dni);
	Optional<Alumno> findByEmail(String email);
	List<Alumno> findByCursoIn(List<Curso> cursos);

	//List<Alumno> findByCursoIdIn(List<Long> cursoIds);
	 @Query("SELECT a FROM Alumno a WHERE a.curso.id IN :cursoIds")
	    List<Alumno> findByCursoIds(@Param("cursoIds") List<Long> cursoIds);
	List<Alumno> findByTutorPracticas(TutorPracticas tutor);

}
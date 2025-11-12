package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.ObservacionDiaria;
import com.control.practicas.models.TutorPracticas;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObservacionDiariaRepository extends JpaRepository<ObservacionDiaria, Long> {
    
    // Buscar por el ID del alumno (relación @ManyToOne)
    List<ObservacionDiaria> findByAlumno_Id(Long alumnoId);
    long countByAlumnoIn(List<Alumno> alumnos);

    
    // Buscar por el ID del alumno ordenado por fecha descendente
    List<ObservacionDiaria> findByAlumno_IdOrderByFechaDesc(Long alumnoId);
    
    // Buscar por fecha
    List<ObservacionDiaria> findByFecha(LocalDate fecha);
    
    // Buscar por rango de fechas
    List<ObservacionDiaria> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar por alumno y fecha
    List<ObservacionDiaria> findByAlumno_IdAndFecha(Long alumnoId, LocalDate fecha);

	List<ObservacionDiaria> findByAlumnoInOrderByFechaDesc(List<Alumno> alumnosDelTutor);
	
	@Query("SELECT COUNT(o) FROM ObservacionDiaria o WHERE o.alumno.tutorPracticas = :tutor")
	long countByTutorPracticas(@Param("tutor") TutorPracticas tutor);
	
	@Query("SELECT SUM(o.horasRealizadas) FROM ObservacionDiaria o WHERE o.alumno.id = :alumnoId")
	Integer sumarHorasRealizadasPorAlumno(@Param("alumnoId") Long alumnoId);


}

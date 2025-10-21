package repositories;

import models.Evaluacion;
import models.Alumno;
import models.TutorPracticas;
import models.CapacidadEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    
    // Buscar evaluaciones por alumno
    List<Evaluacion> findByAlumno(Alumno alumno);
    
    // Buscar evaluaciones por tutor de prácticas
    List<Evaluacion> findByTutorPracticas(TutorPracticas tutorPracticas);
    
    // Buscar evaluaciones por capacidad
    List<Evaluacion> findByCapacidad(CapacidadEvaluacion capacidad);
    
    // Buscar evaluaciones por rango de fechas
    List<Evaluacion> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar evaluaciones por alumno y fecha
    List<Evaluacion> findByAlumnoAndFecha(Alumno alumno, LocalDate fecha);
    
    // Buscar evaluaciones por alumno ordenadas por fecha descendente
    List<Evaluacion> findByAlumnoOrderByFechaDesc(Alumno alumno);
    
    // Query personalizada para obtener evaluaciones con todos los datos relacionados
    @Query("SELECT e FROM Evaluacion e " +
           "LEFT JOIN FETCH e.alumno " +
           "LEFT JOIN FETCH e.tutorPracticas " +
           "LEFT JOIN FETCH e.capacidad " +
           "ORDER BY e.fecha DESC")
    List<Evaluacion> findAllWithRelations();
    
    // Calcular promedio de puntuación por alumno
    @Query("SELECT AVG(e.puntuacion) FROM Evaluacion e WHERE e.alumno.id = :alumnoId")
    Double calcularPromedioAlumno(@Param("alumnoId") Long alumnoId);
}
package repositories;

import models.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByActivoTrue();
    List<Curso> findByTutorCurso_Id(Long tutorCursoId);
    List<Curso> findByCodigo(String codigo);
    
    
 // Nueva consulta para obtener el número de alumnos por curso
    @Query("SELECT COUNT(a) FROM Alumno a WHERE a.curso.id = :cursoId")
    long contarAlumnosPorCurso(Long cursoId);
    
    
}


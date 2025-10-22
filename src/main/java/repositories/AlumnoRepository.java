package repositories;

import models.Alumno;
import models.Curso;
import models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    
    // Este método ya viene heredado de JpaRepository, puedes eliminarlo
    // Optional<Alumno> findById(Long id);
    
    // Métodos que buscan por la entidad completa (están bien)
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByEmpresa(Empresa empresa);
    
    // Métodos que buscan por ID de relaciones (corregidos con guion bajo)
    List<Alumno> findByTutorPracticas_Id(Long tutorPracticasId);
    List<Alumno> findByCurso_Id(Long cursoId);
    List<Alumno> findByEmpresa_Id(Long empresaId);
    
    // Búsqueda por DNI (está bien)
    Optional<Alumno> findByDni(String dni);
}
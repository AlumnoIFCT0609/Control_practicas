package repositories;

import models.TutorCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorCursoRepository extends JpaRepository<TutorCurso, Long> {
    
    /**
     * Buscar tutor por DNI (único)
     */
    Optional<TutorCurso> findByDni(String dni);
    
    /**
     * Buscar tutor por email (único)
     */
    Optional<TutorCurso> findByEmail(String email);
    
    /**
     * Listar solo tutores activos
     */
    List<TutorCurso> findByActivoTrue();
    
    /**
     * Listar tutores inactivos
     */
    List<TutorCurso> findByActivoFalse();
    
    /**
     * Contar tutores activos
     */
    long countByActivoTrue();
    
    /**
     * Buscar tutores por especialidad
     */
    List<TutorCurso> findByEspecialidad(String especialidad);
    
    /**
     * Buscar tutores por nombre (contiene, sin distinción mayúsculas/minúsculas)
     */
    List<TutorCurso> findByNombreContainingIgnoreCase(String nombre);
    
    /**
     * Buscar tutores por apellidos (contiene, sin distinción mayúsculas/minúsculas)
     */
    List<TutorCurso> findByApellidosContainingIgnoreCase(String apellidos);
    
    /**
     * Buscar tutores por nombre o apellidos (contiene)
     */
    List<TutorCurso> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombre, String apellidos);
}

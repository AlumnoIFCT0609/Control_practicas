package repositories;

import models.CapacidadEvaluacion;
import models.CriterioEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacidadEvaluacionRepository extends JpaRepository<CapacidadEvaluacion, Long> {
    
    // Buscar capacidades activas
    List<CapacidadEvaluacion> findByActivoTrue();
    
    // Buscar capacidades por criterio
    List<CapacidadEvaluacion> findByCriterio(CriterioEvaluacion criterio);
    
    // Buscar capacidades activas por criterio
    List<CapacidadEvaluacion> findByCriterioAndActivoTrue(CriterioEvaluacion criterio);
    
    // Query para obtener capacidades con su criterio
    @Query("SELECT c FROM CapacidadEvaluacion c LEFT JOIN FETCH c.criterio WHERE c.activo = true ORDER BY c.criterio.nombre, c.nombre")
    List<CapacidadEvaluacion> findAllActivasWithCriterio();
}
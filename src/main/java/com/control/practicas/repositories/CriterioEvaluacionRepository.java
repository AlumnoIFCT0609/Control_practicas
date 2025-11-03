package com.control.practicas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.CapacidadEvaluacion;
import com.control.practicas.models.CriterioEvaluacion;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {
   
	  // Buscar criterios activos
    List<CriterioEvaluacion> findByActivoTrue();
    
    // Buscar criterios por capacidades
    List<CriterioEvaluacion> findByCapacidades(CapacidadEvaluacion capacidad);
    
    // Buscar criterios activos por capacidades
    List<CriterioEvaluacion> findByCapacidadAndActivoTrue(CapacidadEvaluacion capacidad);
    
    
 // Query para obtener criterios con su capacidad
    @Query("SELECT DISTINCT c FROM CriterioEvaluacion c " +
    	       "LEFT JOIN FETCH c.capacidad cap " +
    	       "WHERE c.activo = true " +
    	       "ORDER BY cap.nombre NULLS LAST, c.nombre")
    	List<CriterioEvaluacion> findAllActivasWithCapacidad();
}


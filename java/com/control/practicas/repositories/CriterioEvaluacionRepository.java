package com.control.practicas.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.control.practicas.models.CapacidadEvaluacion;
import com.control.practicas.models.CriterioEvaluacion;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacion, Long> {
   
	 List<CriterioEvaluacion> findByActivoTrue();
	 //no tiene nada que hacer un criterio, ya que el no conoce evaluacion ni capacidad , son ellos los que le conocen a él
}


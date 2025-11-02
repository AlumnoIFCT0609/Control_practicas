package com.control.practicas.services;

import org.springframework.stereotype.Service;
import com.control.practicas.models.CapacidadEvaluacion;
import com.control.practicas.repositories.CapacidadEvaluacionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CapacidadEvaluacionService {

    private final CapacidadEvaluacionRepository capacidadEvaluacionRepository;

    // 🔹 Inyección de dependencias por constructor
    public CapacidadEvaluacionService(CapacidadEvaluacionRepository capacidadEvaluacionRepository) {
        this.capacidadEvaluacionRepository = capacidadEvaluacionRepository;
    }

    public List<CapacidadEvaluacion> listarTodas() {
        return capacidadEvaluacionRepository.findAll();
    }

    public Optional<CapacidadEvaluacion> buscarPorId(Long id) {
        return capacidadEvaluacionRepository.findById(id);
    }

    public CapacidadEvaluacion guardar(CapacidadEvaluacion capacidad) {
        return capacidadEvaluacionRepository.save(capacidad);
    }

    public void eliminar(Long id) {
        capacidadEvaluacionRepository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return capacidadEvaluacionRepository.existsById(id);
    }
}


package com.control.practicas.services;

import org.springframework.stereotype.Service;
import com.control.practicas.models.CriterioEvaluacion;
import com.control.practicas.repositories.CriterioEvaluacionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CriterioEvaluacionService {

    private final CriterioEvaluacionRepository criterioEvaluacionRepository;

    // 🔹 Inyección por constructor
    public CriterioEvaluacionService(CriterioEvaluacionRepository criterioEvaluacionRepository) {
        this.criterioEvaluacionRepository = criterioEvaluacionRepository;
    }

    public List<CriterioEvaluacion> listarTodos() {
        return criterioEvaluacionRepository.findAll();
    }

    public Optional<CriterioEvaluacion> buscarPorId(Long id) {
        return criterioEvaluacionRepository.findById(id);
    }

    public CriterioEvaluacion guardar(CriterioEvaluacion criterio) {
        return criterioEvaluacionRepository.save(criterio);
    }

    public void eliminar(Long id) {
        criterioEvaluacionRepository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return criterioEvaluacionRepository.existsById(id);
    }
}


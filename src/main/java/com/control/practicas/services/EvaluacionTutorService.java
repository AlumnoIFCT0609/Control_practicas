package com.control.practicas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.control.practicas.models.EvaluacionTutor;
import com.control.practicas.repositories.EvaluacionTutorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionTutorService {

    @Autowired
    private EvaluacionTutorRepository evaluacionTutorRepository;

    public List<EvaluacionTutor> listarTodas() {
        return evaluacionTutorRepository.findAll();
    }

    public Optional<EvaluacionTutor> buscarPorId(Long id) {
        return evaluacionTutorRepository.findById(id);
    }

    public EvaluacionTutor guardar(EvaluacionTutor evaluacion) {
        return evaluacionTutorRepository.save(evaluacion);
    }

    public void eliminar(Long id) {
        evaluacionTutorRepository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return evaluacionTutorRepository.existsById(id);
    }
}

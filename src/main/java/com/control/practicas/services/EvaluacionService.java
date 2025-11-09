package com.control.practicas.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Evaluacion;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.EvaluacionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final AlumnoRepository alumnoRepository;

    // 🔹 Inyección de dependencias por constructor
    public EvaluacionService(EvaluacionRepository evaluacionRepository, AlumnoRepository alumnoRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.alumnoRepository = alumnoRepository;
    }

    public List<Evaluacion> listarTodas() {
        return evaluacionRepository.findAll();
    }

    public Optional<Evaluacion> buscarPorId(Long id) {
        return evaluacionRepository.findById(id);
    }

    public Evaluacion guardar(Evaluacion evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }

    public void eliminar(Long id) {
        evaluacionRepository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return evaluacionRepository.existsById(id);
    }

    public Optional<Alumno> listarAlumnos(long id) {
        return alumnoRepository.findById(id);
    }

    public List<Evaluacion> buscarPorAlumno(Long alumnoId) { 
        return null;
    }

    public List<Evaluacion> buscarPorTutor(Long tutorId) {  
        return null;
    }

    public List<Evaluacion> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {   
        return null;
    }

    public Object listarTutoresPracticas() {
        return null;
    }

    public Object listarCapacidadesActivas() {
        return null;
    }
}


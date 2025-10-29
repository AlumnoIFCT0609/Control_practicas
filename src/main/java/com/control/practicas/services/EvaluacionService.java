package com.control.practicas.services;

import org.springframework.beans.factory.annotation.Autowired;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Evaluacion;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.EvaluacionRepository;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class EvaluacionService {
    @Autowired
    private EvaluacionRepository evaluacionRepository;
    
   
    private AlumnoRepository alumnoRepository;

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
		// TODO Auto-generated method stub
		return null;
	}

	public List<Evaluacion> buscarPorTutor(Long tutorId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Evaluacion> buscarPorRangoFechas(LocalDate inicio, LocalDate fin) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object listarTutoresPracticas() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object listarCapacidadesActivas() {
		// TODO Auto-generated method stub
		return null;
	}
}

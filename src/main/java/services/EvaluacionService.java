package services;

import models.Evaluacion;
import repositories.EvaluacionRepository;

import org.springframework.beans.factory.annotation.Autowired;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class EvaluacionService {
    @Autowired
    private EvaluacionRepository evaluacionRepository;

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
}

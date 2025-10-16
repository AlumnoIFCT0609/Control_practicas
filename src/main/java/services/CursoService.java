package services;

import models.Curso;
import repositories.CursoRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class CursoService {
    
    private final CursoRepository cursoRepository=null;
    
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }
    
    public Optional<Curso> buscarPorId(Long id) {
        return cursoRepository.findById(id);
    }
    
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }
    
    public List<Curso> listarActivos() {
        return cursoRepository.findByActivoTrue();
    }
    
    public List<Curso> listarPorTutorCurso(Long tutorCursoId) {
        return cursoRepository.findByTutorCursoId(tutorCursoId);
    }
    
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }

	public boolean existePorId(Long id) {
		return cursoRepository.existsById(id);
	}
}

package services;

import models.TutorCurso;
import repositories.TutorCursoRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class TutorCursoService {
    
    private final TutorCursoRepository tutorCursoRepository=null;
    
    @Transactional
    public TutorCurso guardar(TutorCurso tutorCurso) {
        return tutorCursoRepository.save(tutorCurso);
    }
    
    public Optional<TutorCurso> buscarPorId(Long id) {
        return tutorCursoRepository.findById(id);
    }
    
    public List<TutorCurso> listarTodos() {
        return tutorCursoRepository.findAll();
    }
    
    @Transactional
    public void eliminar(Long id) {
        tutorCursoRepository.deleteById(id);
    }
}

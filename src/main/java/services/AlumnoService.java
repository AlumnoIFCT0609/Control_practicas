package services;

import models.Alumno;
//import models.Curso;
import repositories.AlumnoRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class AlumnoService {
    
    private final AlumnoRepository alumnoRepository=null;
    
    @Transactional
    public Alumno guardar(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }
    
    public Optional<Alumno> buscarPorId(Long id) {
        return alumnoRepository.findById(id);
    }
    
    public List<Alumno> listarTodos() {
        return alumnoRepository.findAll();
    }
    
    public List<Alumno> listarPorCurso(Long cursoId) {
        return alumnoRepository.findByCursoId(cursoId);
    }
    
    public List<Alumno> listarPorTutorPracticas(Long tutorPracticasId) {
        return alumnoRepository.findByTutorPracticasId(tutorPracticasId);
    }
    
    @Transactional
    public void eliminar(Long id) {
        alumnoRepository.deleteById(id);
    }
}
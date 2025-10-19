package services;

import models.Alumno;
import models.Curso;
import repositories.AlumnoRepository;
import repositories.CursoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {
	
	private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    
    // Constructor para inyección de dependencias
    public AlumnoService(AlumnoRepository alumnoRepository,CursoRepository cursoRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
    }
    
    @Transactional
    public Alumno guardar(Alumno alumno) {
        // Asociar curso persistido
        if (alumno.getCurso() != null && alumno.getCurso().getId() != null) {
            Curso cursoPersistido = cursoRepository.findById(alumno.getCurso().getId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            alumno.setCurso(cursoPersistido);
        }
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
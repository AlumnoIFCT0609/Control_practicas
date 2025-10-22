package services;

import models.Alumno;
import models.Curso;
import models.Empresa;
import models.TutorPracticas;
import repositories.AlumnoRepository;
import repositories.CursoRepository;
import repositories.EmpresaRepository;
import repositories.TutorPracticasRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {
	
    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    
    public AlumnoService(AlumnoRepository alumnoRepository,
                        CursoRepository cursoRepository, 
                        EmpresaRepository empresaRepository,
                        TutorPracticasRepository tutorPracticasRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
        this.empresaRepository = empresaRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
    }
    
    @Transactional
    public Alumno guardar(Alumno alumno) {
        // Validar y asociar curso (obligatorio)
        if (alumno.getCurso() != null && alumno.getCurso().getId() != null) {
            Curso cursoPersistido = cursoRepository.findById(alumno.getCurso().getId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + alumno.getCurso().getId()));
            alumno.setCurso(cursoPersistido);
        } else {
            throw new RuntimeException("El curso es obligatorio");
        }
        
        // Validar y asociar empresa (opcional)
        if (alumno.getEmpresa() != null && alumno.getEmpresa().getId() != null) {
            Empresa empresaPersistida = empresaRepository.findById(alumno.getEmpresa().getId())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + alumno.getEmpresa().getId()));
            alumno.setEmpresa(empresaPersistida);
        }
        
        // Validar y asociar tutor de prácticas (opcional)
        if (alumno.getTutorPracticas() != null && alumno.getTutorPracticas().getId() != null) {
            TutorPracticas tutorPersistido = tutorPracticasRepository.findById(alumno.getTutorPracticas().getId())
                .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado con ID: " + alumno.getTutorPracticas().getId()));
            alumno.setTutorPracticas(tutorPersistido);
        }
        
        // Actualizar fecha de actualización
        alumno.setFechaActualizacion(LocalDateTime.now());
        
        return alumnoRepository.save(alumno);
    }
    
    public Optional<Alumno> buscarPorId(Long id) {
        return alumnoRepository.findById(id);
    }
    
    public List<Alumno> listarTodos() {
        return alumnoRepository.findAll();
    }
    
    public List<Alumno> listarPorCurso(Long cursoId) {
        return alumnoRepository.findByCurso_Id(cursoId);
    }
    
    public List<Alumno> listarPorTutorPracticas(Long tutorPracticasId) {
        return alumnoRepository.findByTutorPracticas_Id(tutorPracticasId);
    }
    
    @Transactional
    public void eliminar(Long id) {
        alumnoRepository.deleteById(id);
    }

    public EmpresaRepository getEmpresaRepository() {
        return empresaRepository;
    }
}
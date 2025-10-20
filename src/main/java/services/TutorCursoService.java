package services;

import models.TutorCurso;
import repositories.TutorCursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TutorCursoService {
    
    private final TutorCursoRepository tutorCursoRepository;
    
    public TutorCursoService(TutorCursoRepository tutorCursoRepository) {
        this.tutorCursoRepository = tutorCursoRepository;
    }
    
    /**
     * Obtener todos los tutores de curso
     */
    public List<TutorCurso> listarTodos() {
        return tutorCursoRepository.findAll();
    }
    
    /**
     * Obtener solo tutores activos
     */
    public List<TutorCurso> listarActivos() {
        return tutorCursoRepository.findByActivoTrue();
    }
    
    /**
     * Buscar tutor por ID
     */
    public Optional<TutorCurso> buscarPorId(Long id) {
        return tutorCursoRepository.findById(id);
    }
    
    /**
     * Buscar tutor por DNI
     */
    public Optional<TutorCurso> buscarPorDni(String dni) {
        return tutorCursoRepository.findByDni(dni);
    }
    
    /**
     * Buscar tutor por email
     */
    public Optional<TutorCurso> buscarPorEmail(String email) {
        return tutorCursoRepository.findByEmail(email);
    }
    
    /**
     * Guardar o actualizar tutor
     */
    public TutorCurso guardar(TutorCurso tutorCurso) {
        return tutorCursoRepository.save(tutorCurso);
    }
    
    /**
     * Eliminar tutor por ID
     */
    public void eliminar(Long id) {
        tutorCursoRepository.deleteById(id);
    }
    
    /**
     * Activar tutor
     */
    public void activar(Long id) {
        Optional<TutorCurso> tutorOpt = tutorCursoRepository.findById(id);
        if (tutorOpt.isPresent()) {
            TutorCurso tutor = tutorOpt.get();
            tutor.setActivo(true);
            tutorCursoRepository.save(tutor);
        }
    }
    
    /**
     * Desactivar tutor
     */
    public void desactivar(Long id) {
        Optional<TutorCurso> tutorOpt = tutorCursoRepository.findById(id);
        if (tutorOpt.isPresent()) {
            TutorCurso tutor = tutorOpt.get();
            tutor.setActivo(false);
            tutorCursoRepository.save(tutor);
        }
    }
    
    /**
     * Verificar si existe tutor con el DNI
     */
    public boolean existePorDni(String dni) {
        return tutorCursoRepository.findByDni(dni).isPresent();
    }
    
    /**
     * Verificar si existe tutor con el email
     */
    public boolean existePorEmail(String email) {
        return tutorCursoRepository.findByEmail(email).isPresent();
    }
    
    /**
     * Contar total de tutores
     */
    public long contarTodos() {
        return tutorCursoRepository.count();
    }
    
    /**
     * Contar tutores activos
     */
    public long contarActivos() {
        return tutorCursoRepository.countByActivoTrue();
    }
    
    /**
     * Buscar tutores por especialidad
     */
    public List<TutorCurso> buscarPorEspecialidad(String especialidad) {
        return tutorCursoRepository.findByEspecialidad(especialidad);
    }
    
    /**
     * Buscar tutores por nombre o apellidos
     */
    public List<TutorCurso> buscarPorNombreOApellidos(String texto) {
        return tutorCursoRepository.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(texto, texto);
    }
}
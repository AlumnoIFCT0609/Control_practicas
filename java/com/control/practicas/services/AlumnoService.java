package com.control.practicas.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.models.Usuario;
import com.control.practicas.models.Usuario.Rol;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.CursoRepository;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import com.control.practicas.repositories.TutorPracticasRepository;
import com.control.practicas.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService {

    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final ObservacionDiariaRepository observacionDiariaRepository;
 // 🔹 Inyección de dependencias por constructor
    public AlumnoService(AlumnoRepository alumnoRepository,
                        CursoRepository cursoRepository, 
                        EmpresaRepository empresaRepository,
                        ObservacionDiariaRepository observacionDiariaRepository,
                        TutorPracticasRepository tutorPracticasRepository,
                        UsuarioService usuarioService,
                        UsuarioRepository usuarioRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cursoRepository = cursoRepository;
        this.empresaRepository = empresaRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.observacionDiariaRepository=observacionDiariaRepository;
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
    
    
    
    
    public Alumno obtenerPorEmail(String email) {
        return alumnoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No se encontró el alumno con email: " + email));
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
    
    // Buscar alumno por usuario
    public Optional<Alumno> findByUsuario(Usuario usuario) {
        if (usuario.getRol() == Rol.ALUMNO) {
            return alumnoRepository.findById(usuario.getReferenceId());
        }
        return Optional.empty();
    }
    
    // Buscar alumno por email del usuario
    public Optional<Alumno> findByEmailUsuario(String email) {
        return usuarioRepository.findByEmail(email)
            .filter(u -> u.getRol() == Rol.ALUMNO)
            .flatMap(u -> alumnoRepository.findById(u.getReferenceId()));
    }
    
    public List<Alumno> listarTodosConUsuario() {
        List<Alumno> alumnos = alumnoRepository.findAll();
        alumnos.forEach(alumno -> {
            boolean tieneUsuario = usuarioService.existeUsuarioPorEmail(alumno.getEmail());
            alumno.setTieneUsuario(tieneUsuario);
        });
        return alumnos;
    }
    
    /**
     * Crea un usuario para el alumno
     */
    public Usuario crearUsuarioParaAlumno(Long alumnoId) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
            .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
       
        return usuarioService.crearUsuarioParaEntidad(
            alumno.getEmail(),
            alumno.getDni(),
            Rol.ALUMNO,
            alumno.getId(),
            alumno.isActivo()
        );
    }
    
    public int contarObservacionesPorAlumno(Alumno alumno) {
        if (alumno == null) return 0;
        return observacionDiariaRepository.findByAlumno_Id(alumno.getId()).size();
    }

    
}
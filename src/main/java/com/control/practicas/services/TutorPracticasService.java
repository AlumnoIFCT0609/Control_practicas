package com.control.practicas.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.models.Usuario;
import com.control.practicas.models.Usuario.Rol;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.IncidenciaRepository;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import com.control.practicas.repositories.TutorCursoRepository;
import com.control.practicas.repositories.TutorPracticasRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class TutorPracticasService {
	
    private final AlumnoRepository alumnoRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final IncidenciaRepository incidenciaRepository;
    private final TutorCursoRepository tutorCursoRepository;

    private final TutorPracticasRepository tutorPracticasRepository;
    private final UsuarioService usuarioService;
 
 // 🔹 Inyección de dependencias por constructor
    public TutorPracticasService(TutorPracticasRepository tutorPracticasRepository,
		 						UsuarioService usuarioService,AlumnoRepository alumnoRepository,
                                ObservacionDiariaRepository observacionDiariaRepository,
                                TutorCursoRepository tutorCursoRepository,
                                IncidenciaRepository incidenciaRepository) {
    	this.tutorPracticasRepository = tutorPracticasRepository;
    	this.usuarioService = usuarioService;
    	this.alumnoRepository = alumnoRepository;
    	this.observacionDiariaRepository = observacionDiariaRepository;
    	this.incidenciaRepository = incidenciaRepository;
    	this.tutorCursoRepository=tutorCursoRepository;
    }
 
 
    public TutorPracticas guardar(TutorPracticas tutorPracticas) {
    	return tutorPracticasRepository.save(tutorPracticas);
    }
 
    public Optional<TutorPracticas> buscarPorId(Long id) {
    	return tutorPracticasRepository.findById(id);
    }
    public Optional<TutorPracticas> buscarPorDni(String dni) {
    	return tutorPracticasRepository.findByDni(dni);
    }
 
    public List<TutorPracticas> listarTodos() {
    	return tutorPracticasRepository.findAll();
    }
 
    public List<TutorPracticas> listarPorEmpresa(Long empresaId) {
    	return tutorPracticasRepository.findByEmpresaId(empresaId);
    }
 
    @Transactional
    public void eliminar(Long id) {
    	tutorPracticasRepository.deleteById(id);
    }
    public Usuario crearUsuarioParaTutorPracticas(Long tutorId) {
	    	TutorPracticas tutor = tutorPracticasRepository.findById(tutorId)
	    			.orElseThrow(() -> new IllegalArgumentException("Tutor de prácticas no encontrado"));
	    
	    	return usuarioService.crearUsuarioParaEntidad(
	    													tutor.getEmail(),
	    													tutor.getDni(),
	    													Rol.TUTOR_PRACTICAS,
	    													tutor.getId(),
	    													tutor.getActivo()
	    													);
	}
    public long contarObservaciones(TutorPracticas tutor) {
    	List<Alumno> alumnos = alumnoRepository.findByTutorPracticas(tutor);
    	return alumnos.isEmpty() ? 0 : observacionDiariaRepository.countByAlumnoIn(alumnos);
    }

    public long contarIncidencias(TutorPracticas tutor) {
    	List<Alumno> alumnos = alumnoRepository.findByTutorPracticas(tutor);
    	return alumnos.isEmpty() ? 0 : incidenciaRepository.countByAlumnoIn(alumnos);
    }

    // podemos devolver la lista de alumnos del tutor
    public List<Alumno> listarAlumnos(TutorPracticas tutor) {
    	return alumnoRepository.findByTutorPracticas(tutor);
    }
 
    public int obtenerTotalHorasAlumno(Long alumnoId) {
    	Integer horas = observacionDiariaRepository.sumarHorasRealizadasPorAlumno(alumnoId);
    	return horas != null ? horas : 0;
	}

    public Map<String, Integer> obtenerHorasPorAlumno(List<Alumno> alumnos) {
    		Map<String, Integer> horasPorAlumno = new HashMap<>();
    		for (Alumno alumno : alumnos) {
    			int horas = obtenerTotalHorasAlumno(alumno.getId());
    			horasPorAlumno.put(alumno.getId().toString(), horas); // clave como String
    		}
    		return horasPorAlumno;
	}

    public Map<Long, Integer> obtenerHorasPendientesPorAlumno(List<Alumno> alumnos) {
    		Map<Long, Integer> horasPendientes = new HashMap<>();
    		for (Alumno alumno : alumnos) {
    			int horasRealizadas = obtenerTotalHorasAlumno(alumno.getId());
    			int pendientes = alumno.getDuracionPracticas() - horasRealizadas;
    			if (pendientes < 0) pendientes = 0;
    			horasPendientes.put(alumno.getId(), pendientes);
    		}
    		return horasPendientes;
	}

    public long contarActivos() {
    	return tutorCursoRepository.countByActivoTrue();
    }
 
}
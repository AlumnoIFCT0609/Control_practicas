package controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import models.Alumno;
import models.ObservacionDiaria;
import models.TutorPracticas;
import models.Usuario;
import repositories.AlumnoRepository;
import repositories.ObservacionDiariaRepository;
import repositories.TutorPracticasRepository;
import repositories.UsuarioRepository;
import services.AlumnoService;
import services.ObservacionDiariaService;
import services.TutorPracticasService;

@Controller
@RequestMapping("/tutorpracticas")
public class TutorPracticasDatoController {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final UsuarioRepository usuarioRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final TutorPracticasService tutorPracticasService;
    
    public TutorPracticasDatoController(
            ObservacionDiariaRepository observacionDiariaRepository,
            ObservacionDiariaService observacionDiariaService,
            AlumnoRepository alumnoRepository,
            AlumnoService alumnoService,
            UsuarioRepository usuarioRepository,
            TutorPracticasRepository tutorPracticasRepository,
            TutorPracticasService tutorPracticasService) {
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.alumnoRepository = alumnoRepository;
        this.alumnoService = alumnoService;
        this.usuarioRepository = usuarioRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        this.tutorPracticasService = tutorPracticasService;
    }
    
    // Método auxiliar para obtener el tutor de prácticas autenticado
    private TutorPracticas getTutorAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return tutorPracticasRepository.findById(user.getReferenceId())
            .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado"));
    }
    /*
    @GetMapping("/observaciondiaria/listar")
    public String listar(
            @RequestParam(required = false) Long alumnoId,
            Model model, 
            Authentication authentication) {
        
        TutorPracticas tutor = getTutorAutenticado(authentication);
        
        // Obtener todos los alumnos del tutor
        List<Alumno> alumnosDelTutor = alumnoRepository.findByTutorPracticas(tutor);
        
        List<ObservacionDiaria> observaciones;
        Alumno alumnoSeleccionado = null;
        
        if (alumnoId != null) {
            // Filtrar por alumno específico
            alumnoSeleccionado = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
            
            // Verificar que el alumno pertenece al tutor
            if (!alumnoSeleccionado.getTutorPracticas().getId().equals(tutor.getId())) {
                throw new RuntimeException("No tiene permisos para ver las observaciones de este alumno");
            }
            
            observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumnoId);
        } else {
            // Mostrar observaciones de todos sus alumnos
            observaciones = observacionDiariaRepository.findByAlumnoInOrderByFechaDesc(alumnosDelTutor);
        }
        
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("alumnosDelTutor", alumnosDelTutor);
        model.addAttribute("alumnoSeleccionado", alumnoSeleccionado);
        model.addAttribute("tutorActual", tutor);
        model.addAttribute("tieneObservaciones", !observaciones.isEmpty());
        model.addAttribute("viewName", "tutorpracticas/observaciondiaria/listar");
        return "layout";
    }
    */
    
    
    @GetMapping("/observaciondiaria/listar")
    public String listar(
            @RequestParam(required = false) Long alumnoId,
            Model model, 
            Authentication authentication) {
        
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);
            
            // Obtener todos los alumnos del tutor
            List<Alumno> alumnosDelTutor = alumnoRepository.findByTutorPracticas(tutor);
            
            System.out.println("Tutor: " + tutor.getNombre());
            System.out.println("Alumnos del tutor: " + alumnosDelTutor.size());
            
            List<ObservacionDiaria> observaciones;
            Alumno alumnoSeleccionado = null;
            
            if (alumnoId != null) {
                // Filtrar por alumno específico
                alumnoSeleccionado = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
                
                // Verificar que el alumno pertenece al tutor
                if (!alumnoSeleccionado.getTutorPracticas().getId().equals(tutor.getId())) {
                    throw new RuntimeException("No tiene permisos para ver las observaciones de este alumno");
                }
                
                observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumnoId);
            } else {
                // Mostrar observaciones de todos sus alumnos
                if (alumnosDelTutor.isEmpty()) {
                    observaciones = List.of(); // Lista vacía si no tiene alumnos
                } else {
                    observaciones = observacionDiariaRepository.findByAlumnoInOrderByFechaDesc(alumnosDelTutor);
                }
            }
            
            System.out.println("Observaciones encontradas: " + observaciones.size());
            
            model.addAttribute("observaciones", observaciones);
            model.addAttribute("alumnosDelTutor", alumnosDelTutor);
            model.addAttribute("alumnoSeleccionado", alumnoSeleccionado);
            model.addAttribute("tutorActual", tutor);
            model.addAttribute("tieneObservaciones", !observaciones.isEmpty());
            model.addAttribute("viewName", "tutorpracticas/observaciondiaria/listar");
            return "layout";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("viewName", "error");
            return "layout";
        }
    }
    @GetMapping("/observaciondiaria/ver/{id}")
    public String ver(@PathVariable Long id, Model model, Authentication authentication) {
        TutorPracticas tutor = getTutorAutenticado(authentication);
        ObservacionDiaria observacionDiaria = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
        
        // Verificar que la observación pertenece a un alumno del tutor
        if (!observacionDiaria.getAlumno().getTutorPracticas().getId().equals(tutor.getId())) {
            throw new RuntimeException("No tiene permisos para ver esta observación");
        }
        
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", observacionDiaria.getAlumno());
        model.addAttribute("tutorActual", tutor);
        model.addAttribute("soloLectura", false); // El tutor puede editar sus observaciones
        model.addAttribute("viewName", "tutorpracticas/observaciondiaria/form");
        return "layout";
    }
    
    @PostMapping("/observaciondiaria/guardar")
    public String guardar(
            @ModelAttribute ObservacionDiaria observacionDiaria,
            @RequestParam(required = false) String observacionesTutor,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);
            
            // Buscar la observación existente
            ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacionDiaria.getId())
                .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
            
            // Verificar que el alumno pertenece al tutor
            if (!observacionExistente.getAlumno().getTutorPracticas().getId().equals(tutor.getId())) {
                throw new RuntimeException("No tiene permisos para editar esta observación");
            }
            
            // Solo actualizar las observaciones del tutor (no tocar datos del alumno)
            observacionExistente.setObservacionesTutor(observacionesTutor);
            
            observacionDiariaService.guardar(observacionExistente);
            redirectAttributes.addFlashAttribute("success", "Observaciones del tutor actualizadas exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar las observaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tutorpracticas/observaciondiaria/listar";
    }
}
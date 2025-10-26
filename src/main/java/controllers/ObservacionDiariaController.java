package controllers;

import models.*;
import repositories.*;
import services.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/alumno/observaciondiaria")
public class ObservacionDiariaController {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public ObservacionDiariaController(ObservacionDiariaRepository observacionDiariaRepository,
                                      ObservacionDiariaService observacionDiariaService,
                                      AlumnoRepository alumnoRepository,
                                      UsuarioRepository usuarioRepository) {
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    
    // Método auxiliar para obtener el alumno autenticado
    private Alumno getAlumnoAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // El referenceId del usuario apunta al ID del alumno
        return alumnoRepository.findById(user.getReferenceId())
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
    }
    
    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        List<ObservacionDiaria> observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumno.getId());
        
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/listar");
        return "layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        
        model.addAttribute("observacion", new ObservacionDiaria());
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        ObservacionDiaria observacion = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
        
        // Verificar que la observación pertenece al alumno autenticado
        if (!observacion.getAlumno().getId().equals(alumno.getId())) {
            throw new RuntimeException("No tiene permisos para editar esta observación");
        }
        
        model.addAttribute("observacion", observacion);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ObservacionDiaria observacion, 
                         Authentication authentication, 
                         RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            
            // Si es edición, verificar que pertenece al alumno
            if (observacion.getId() != null) {
                ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacion.getId())
                    .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
                
                if (!observacionExistente.getAlumno().getId().equals(alumno.getId())) {
                    throw new RuntimeException("No tiene permisos para editar esta observación");
                }
                
                // Preservar las observaciones del tutor
                observacion.setObservacionesTutor(observacionExistente.getObservacionesTutor());
            }
            
            // Asignar el alumno autenticado
            observacion.setAlumno(alumno);
            
            observacionDiariaService.guardar(observacion);
            redirectAttributes.addFlashAttribute("success", 
                    observacion.getId() == null ? "Observación creada exitosamente" : "Observación actualizada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la observación: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/alumno/observaciondiaria/listar";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            Optional<ObservacionDiaria> observacionOpt = observacionDiariaService.buscarPorId(id);
            
            if (observacionOpt.isPresent()) {
                ObservacionDiaria observacion = observacionOpt.get();
                
                // Verificar que pertenece al alumno
                if (!observacion.getAlumno().getId().equals(alumno.getId())) {
                    throw new RuntimeException("No tiene permisos para eliminar esta observación");
                }
                
                observacionDiariaService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Observación eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Observación no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la observación: " + e.getMessage());
        }
        return "redirect:/alumno/observaciondiaria/listar";
    }
}
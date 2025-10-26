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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import models.Alumno;
import models.ObservacionDiaria;
import models.Usuario;
import repositories.AlumnoRepository;
import repositories.ObservacionDiariaRepository;
import repositories.UsuarioRepository;
import services.AlumnoService;
import services.ObservacionDiariaService;

@Controller
@RequestMapping("/alumno")
public class AlumnoDatoController {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final UsuarioRepository usuarioRepository;
    
    public AlumnoDatoController(ObservacionDiariaRepository observacionDiariaRepository,
                                      ObservacionDiariaService observacionDiariaService,
                                      AlumnoRepository alumnoRepository,
                                      AlumnoService alumnoService,
                                      UsuarioRepository usuarioRepository) {
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alumnoService= alumnoService;
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
    
    @GetMapping("/perfil")
    public String misDatos(RedirectAttributes redirectAttrs, Authentication authentication) {
        String email = authentication.getName();
        try {
            Alumno alumno = alumnoService.obtenerPorEmail(email);
            if (alumno == null) {
                redirectAttrs.addFlashAttribute("alerta", "No se encontró ningún alumno con el email: " + email);
                return "redirect:/alumno/dashboard"; // redirige al dashboard
            }
            redirectAttrs.addFlashAttribute("alumno", alumno);
            return "redirect:/alumno/misdatos/form"; // o la vista que corresponda
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "No se encontró el alumno con el email: " + email);
            return "redirect:/alumno/dashboard";
        }
    }
    
    
    @GetMapping("/observaciondiaria/listar")
    public String listar(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        List<ObservacionDiaria> observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumno.getId());
        
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("tieneObservaciones", !observaciones.isEmpty());
        model.addAttribute("viewName", "alumno/observaciondiaria/listar");
        return "layout";
    }
    
    @GetMapping("/observaciondiaria/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        ObservacionDiaria observacionDiaria = new ObservacionDiaria();
       // model.addAttribute("observacionDiaria", new ObservacionDiaria());
        observacionDiaria.setHorasRealizadas(0); // ← INICIALIZA ESTO
        observacionDiaria.setFecha(LocalDate.now());
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    @GetMapping("/observaciondiaria/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        ObservacionDiaria observacionDiaria = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
        
        // Verificar que la observación pertenece al alumno autenticado
        if (!observacionDiaria.getAlumno().getId().equals(alumno.getId())) {
            throw new RuntimeException("No tiene permisos para editar esta observación");
        }
        
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    @PostMapping("/observaciondiaria/guardar")
    public String guardar(@ModelAttribute ObservacionDiaria observacionDiaria, 
                         Authentication authentication, 
                         RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            
            // Si es edición, verificar que pertenece al alumno
            if (observacionDiaria.getId() != null) {
                ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacionDiaria.getId())
                    .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
                
                if (!observacionExistente.getAlumno().getId().equals(alumno.getId())) {
                    throw new RuntimeException("No tiene permisos para editar esta observación");
                }
                
                // Preservar las observaciones del tutor
                observacionDiaria.setObservacionesTutor(observacionExistente.getObservacionesTutor());
            }
            
            // Asignar el alumno autenticado
            observacionDiaria.setAlumno(alumno);
            
            observacionDiariaService.guardar(observacionDiaria);
            redirectAttributes.addFlashAttribute("success", 
                    observacionDiaria.getId() == null ? "Observación creada exitosamente" : "Observación actualizada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la observación: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/alumno/observaciondiaria/listar";
    }
    
    @GetMapping("/observaciondiaria/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            Optional<ObservacionDiaria> observacionOpt = observacionDiariaService.buscarPorId(id);
            
            if (observacionOpt.isPresent()) {
                ObservacionDiaria observacionDiaria = observacionOpt.get();
                
                // Verificar que pertenece al alumno
                if (!observacionDiaria.getAlumno().getId().equals(alumno.getId())) {
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
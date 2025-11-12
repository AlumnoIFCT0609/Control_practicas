package com.control.practicas.controllers;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.EvaluacionTutor;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.services.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/alumno/evaluaciontutor")
@PreAuthorize("hasAuthority('ALUMNO')")
public class AlumnoEvaluacionTutorController {

    private final EvaluacionTutorService evaluacionTutorService;
    private final AlumnoService alumnoService;

    public AlumnoEvaluacionTutorController(EvaluacionTutorService evaluacionTutorService,
                                           AlumnoService alumnoService) {
        this.evaluacionTutorService = evaluacionTutorService;
        this.alumnoService = alumnoService;
    }

    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        String username = authentication.getName();
        Alumno alumno = alumnoService.obtenerPorEmail(username);
        
        List<EvaluacionTutor> evaluaciones = 
                evaluacionTutorService.listarPorTutorPracticas(alumno.getTutorPracticas().getId());
        
        // Determinar permisos
        boolean puedeCrear = evaluaciones.isEmpty();
        boolean puedeEditar = false;
        
        if (!evaluaciones.isEmpty()) {
            EvaluacionTutor evaluacion = evaluaciones.get(0);
            puedeEditar = evaluacion.getFecha().equals(LocalDate.now());
        }
        
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("puedeCrear", puedeCrear);
        model.addAttribute("puedeEditar", puedeEditar);
        model.addAttribute("puedeEliminar", false);
        model.addAttribute("viewName", "evaluaciontutor/listar");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Alumno alumno = alumnoService.obtenerPorEmail(username);
        TutorPracticas tutorPracticas = alumno.getTutorPracticas();
        
        // Verificar si ya existe evaluación
        List<EvaluacionTutor> evaluacionesExistentes =
                evaluacionTutorService.listarPorTutorPracticas(tutorPracticas.getId());
        
        if (!evaluacionesExistentes.isEmpty()) {
            redirectAttributes.addFlashAttribute("warning", "Ya has realizado una evaluación.");
            return "redirect:/alumno/evaluaciontutor/listar";
        }
        
        // Crear nueva evaluación
        EvaluacionTutor evaluacion = new EvaluacionTutor();
        evaluacion.setFecha(LocalDate.now());
        evaluacion.setTutorCurso(alumno.getCurso().getTutorCurso());
        evaluacion.setTutorPracticas(tutorPracticas);
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("soloLectura", false);
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, 
                         Model model, 
                         Authentication authentication, 
                         RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Alumno alumno = alumnoService.obtenerPorEmail(username);
        
        EvaluacionTutor evaluacion = evaluacionTutorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
        
        // Verificar que es su evaluación
        if (!evaluacion.getTutorPracticas().getId().equals(alumno.getTutorPracticas().getId())) {
            redirectAttributes.addFlashAttribute("error", "No puedes acceder a esta evaluación.");
            return "redirect:/alumno/evaluaciontutor/listar";
        }
        
        // Verificar que fue creada hoy
        if (!evaluacion.getFecha().equals(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("info", 
                "Solo puedes visualizar esta evaluación. No se puede modificar después del día de creación.");
            return "redirect:/alumno/evaluaciontutor/listar";
        }
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("soloLectura", false);
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute EvaluacionTutor evaluacion, 
                          Authentication authentication, 
                          RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        Alumno alumno = alumnoService.obtenerPorEmail(username);
        
        try {
            // Si está editando, verificar permisos
            if (evaluacion.getId() != null) {
                EvaluacionTutor evaluacionExistente = evaluacionTutorService.buscarPorId(evaluacion.getId())
                        .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
                
                // Verificar que es suya
                if (!evaluacionExistente.getTutorPracticas().getId().equals(alumno.getTutorPracticas().getId())) {
                    redirectAttributes.addFlashAttribute("error", "No puedes modificar esta evaluación.");
                    return "redirect:/alumno/evaluaciontutor/listar";
                }
                
                // Verificar que fue creada hoy
                if (!evaluacionExistente.getFecha().equals(LocalDate.now())) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Solo puedes modificar la evaluación el mismo día de su creación.");
                    return "redirect:/alumno/evaluaciontutor/listar";
                }
                
                // Mantener la fecha original
                evaluacion.setFecha(evaluacionExistente.getFecha());
            } else {
                // Si es nueva, verificar que no exista ya una
                List<EvaluacionTutor> evaluacionesExistentes = 
                        evaluacionTutorService.listarPorTutorPracticas(alumno.getTutorPracticas().getId());
                
                if (!evaluacionesExistentes.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Ya has creado una evaluación para este tutor.");
                    return "redirect:/alumno/evaluaciontutor/listar";
                }
                
                // Establecer fecha actual
                evaluacion.setFecha(LocalDate.now());
            }
            
            // Asegurar que la evaluación esté vinculada correctamente
            evaluacion.setTutorPracticas(alumno.getTutorPracticas());
            evaluacion.setTutorCurso(alumno.getCurso().getTutorCurso());
            
            evaluacionTutorService.guardar(evaluacion);
            
            String mensaje = evaluacion.getId() == null 
                ? "Evaluación creada correctamente." 
                : "Evaluación actualizada correctamente.";
            redirectAttributes.addFlashAttribute("success", mensaje);
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        
        return "redirect:/alumno/evaluaciontutor/listar";
    }
}

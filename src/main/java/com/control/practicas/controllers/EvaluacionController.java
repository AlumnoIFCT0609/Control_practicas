package com.control.practicas.controllers;

import com.control.practicas.models.*;
import com.control.practicas.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("admin/evaluaciones")
public class EvaluacionController {
    
    private final EvaluacionService evaluacionService;
    private final CapacidadEvaluacionService capacidadService;
    private final CriterioEvaluacionService criterioService;
    // Asumiendo que tienes estos servicios
    private final AlumnoService alumnoService;
    private final TutorPracticasService tutorService;
    
    public EvaluacionController(
            EvaluacionService evaluacionService,
            CapacidadEvaluacionService capacidadService,
            CriterioEvaluacionService criterioService,
            AlumnoService alumnoService,
            TutorPracticasService tutorService) {
        this.evaluacionService = evaluacionService;
        this.capacidadService = capacidadService;
        this.criterioService = criterioService;
        this.alumnoService = alumnoService;
        this.tutorService = tutorService;
    }
    
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Evaluacion> evaluaciones = evaluacionService.listarTodas();
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("titulo", "Listado de Evaluaciones");
        model.addAttribute("viewName", "admin/evaluacion/listar");
        return "layout";
       
    }
    
    @GetMapping("/nueva")
    public String nuevaEvaluacion(Model model) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setFecha(LocalDate.now());
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("titulo", "Nueva Evaluación");
        model.addAttribute("accion", "nueva");
        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
        
    }
    
    @GetMapping("/editar/{id}")
    public String editarEvaluacion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Evaluacion evaluacion = evaluacionService.buscarPorId(id).orElse(null);
        
        if (evaluacion == null) {
            flash.addFlashAttribute("error", "La evaluación no existe");
            return "redirect:/admin/evaluaciones/listar";
        }
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("titulo", "Editar Evaluación");
        model.addAttribute("accion", "editar");
        
        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Evaluacion evaluacion, RedirectAttributes flash) {
        try {
            evaluacionService.guardar(evaluacion);
            flash.addFlashAttribute("success", "Evaluación guardada correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
        }
        return "redirect:/admin/evaluaciones/listar";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            evaluacionService.eliminar(id);
            flash.addFlashAttribute("success", "Evaluación eliminada correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar la evaluación");
        }
        return "redirect:/admin/evaluaciones/listar";
    }
    
    // Endpoint AJAX para obtener capacidades por criterio
    @GetMapping("/capacidades/{criterioId}")
    @ResponseBody
    public List<CapacidadEvaluacion> obtenerCapacidadesPorCriterio(@PathVariable Long criterioId) {
        CriterioEvaluacion criterio = criterioService.buscarPorId(criterioId).orElse(null);
        if (criterio != null) {
            return capacidadService.listarPorCriterio(criterio);
        }
        return List.of();
    }
}

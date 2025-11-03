package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.control.practicas.models.Evaluacion;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.CapacidadEvaluacionService;
import com.control.practicas.services.EvaluacionService;
import com.control.practicas.services.TutorPracticasService;

import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion")
public class EvaluacionWebController {

    private final EvaluacionService evaluacionService;
    private final AlumnoService alumnoService;
    private final TutorPracticasService tutorService;
    private final CapacidadEvaluacionService capacidadEvaluacionService;
    public EvaluacionWebController(EvaluacionService evaluacionService, 
                                    AlumnoService alumnoService,
                                    TutorPracticasService tutorService,
                                    CapacidadEvaluacionService capacidadEvaluacionService) {
        this.evaluacionService = evaluacionService;
        this.alumnoService = alumnoService;
        this.tutorService = tutorService;
        this.capacidadEvaluacionService = capacidadEvaluacionService;

    }

    @GetMapping("/listar")
    public String listar(Model model) {
        List<Evaluacion> evaluaciones = evaluacionService.listarTodas();
        model.addAttribute("evaluaciones", evaluaciones);
       // model.addAttribute("viewName", "enconstruccion"); // a cambiar
        model.addAttribute("viewName", "admin/evaluacion/listar");
        return "layout";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("capacidades", capacidadEvaluacionService.listarTodas());
        model.addAttribute("soloLectura", false); // <-- AÑADE ESTO
        return "admin/evaluacion/form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return evaluacionService.buscarPorId(id)
            .map(evaluacion -> {
                model.addAttribute("evaluacion", evaluacion);
                model.addAttribute("alumnos", alumnoService.listarTodos());
                model.addAttribute("tutores", tutorService.listarTodos());
                model.addAttribute("capacidades", capacidadEvaluacionService.listarTodas());
                return "admin/evaluacion/evaluacion-form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Evaluación no encontrada");
                return "redirect:/admin/evaluacion";
            });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Evaluacion evaluacion, RedirectAttributes redirectAttributes) {
        try {
            evaluacionService.guardar(evaluacion);
            redirectAttributes.addFlashAttribute("success", "Evaluación guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
        }
        
        return "redirect:/admin/evaluacion";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (evaluacionService.existePorId(id)) {
                evaluacionService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Evaluación eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Evaluación no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la evaluación: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion";
    }
}

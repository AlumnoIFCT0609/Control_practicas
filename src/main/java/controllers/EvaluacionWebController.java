package controllers;

import models.Evaluacion;
import services.EvaluacionService;
import services.AlumnoService;
import services.TutorPracticasService;
import services.CapacidadEvaluacionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion")
public class EvaluacionWebController {

    private final EvaluacionService evaluacionService;
    private final AlumnoService alumnoService;
    private final TutorPracticasService tutorService;
    private final CapacidadEvaluacionService capacidadService;

    public EvaluacionWebController(EvaluacionService evaluacionService, 
                                    AlumnoService alumnoService,
                                    TutorPracticasService tutorService,
                                    CapacidadEvaluacionService capacidadService) {
        this.evaluacionService = evaluacionService;
        this.alumnoService = alumnoService;
        this.tutorService = tutorService;
        this.capacidadService = capacidadService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Evaluacion> evaluaciones = evaluacionService.listarTodas();
        model.addAttribute("evaluaciones", evaluaciones);
        return "admin/evaluacion/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("capacidades", capacidadService.listarTodas());
        return "admin/evaluacion/evaluacion-form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return evaluacionService.buscarPorId(id)
            .map(evaluacion -> {
                model.addAttribute("evaluacion", evaluacion);
                model.addAttribute("alumnos", alumnoService.listarTodos());
                model.addAttribute("tutores", tutorService.listarTodos());
                model.addAttribute("capacidades", capacidadService.listarTodas());
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

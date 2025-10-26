package controllers;

import models.CapacidadEvaluacion;
import models.CriterioEvaluacion;
import services.CapacidadEvaluacionService;
import services.CriterioEvaluacionService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion/capacidad")
public class CapacidadEvaluacionController {

    private final CapacidadEvaluacionService capacidadService;
    private final CriterioEvaluacionService criterioService;

    public CapacidadEvaluacionController(CapacidadEvaluacionService capacidadService, 
                                         CriterioEvaluacionService criterioService) {
        this.capacidadService = capacidadService;
        this.criterioService = criterioService;
    }

    @GetMapping
    public String listar(Model model) {
        List<CapacidadEvaluacion> capacidades = capacidadService.listarTodas();
        model.addAttribute("capacidades", capacidades);
        return "admin/evaluacion/capacidades";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("capacidad", new CapacidadEvaluacion());
        model.addAttribute("criterios", criterioService.listarTodos());
        return "admin/evaluacion/capacidad-form";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return capacidadService.buscarPorId(id)
            .map(capacidad -> {
                model.addAttribute("capacidad", capacidad);
                model.addAttribute("criterios", criterioService.listarTodos());
                return "admin/evaluacion/capacidad-form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Capacidad no encontrada");
                return "redirect:/admin/evaluacion/capacidad";
            });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CapacidadEvaluacion capacidad, RedirectAttributes redirectAttributes) {
        try {
            capacidadService.guardar(capacidad);
            redirectAttributes.addFlashAttribute("success", "Capacidad guardada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la capacidad: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion/capacidad";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (capacidadService.existePorId(id)) {
                capacidadService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Capacidad eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Capacidad no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la capacidad: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion/capacidad";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return capacidadService.buscarPorId(id)
            .map(capacidad -> {
                capacidad.setActivo(!capacidad.getActivo());
                capacidadService.guardar(capacidad);
                redirectAttributes.addFlashAttribute("success", 
                    capacidad.getActivo() ? "Capacidad activada" : "Capacidad desactivada");
                return "redirect:/admin/evaluacion/capacidad";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Capacidad no encontrada");
                return "redirect:/admin/evaluacion/capacidad";
            });
    }
}

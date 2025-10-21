package controllers;

import models.Evaluacion;
import services.EvaluacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion")
public class EvaluacionWebController {

    @Autowired
    private EvaluacionService evaluacionService;

    @GetMapping("/listar")
    public String listar(
            @RequestParam(required = false) Long alumnoId,
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            Model model) {
        
        List<Evaluacion> evaluaciones;
        
        // Aplicar filtros si existen
        if (alumnoId != null) {
            evaluaciones = evaluacionService.buscarPorAlumno(alumnoId);
        } else if (tutorId != null) {
            evaluaciones = evaluacionService.buscarPorTutor(tutorId);
        } else if (fechaInicio != null && fechaFin != null && !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            evaluaciones = evaluacionService.buscarPorRangoFechas(inicio, fin);
        } else {
            evaluaciones = evaluacionService.listarTodas();
        }
        
        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("alumnos", evaluacionService.listarTodas());
        model.addAttribute("tutores", evaluacionService.listarTutoresPracticas());
        model.addAttribute("viewName", "admin/evaluacion/listar");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("evaluacion", new Evaluacion());
        model.addAttribute("alumnos", evaluacionService.listarTodas());
        model.addAttribute("tutores", evaluacionService.listarTutoresPracticas());
        model.addAttribute("capacidades", evaluacionService.listarCapacidadesActivas());
        model.addAttribute("soloLectura", false);
        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
    }

    @PostMapping("/nuevo")
    public String guardarNuevo(@ModelAttribute Evaluacion evaluacion, 
                               RedirectAttributes redirectAttributes) {
        try {
            evaluacionService.guardar(evaluacion);
            redirectAttributes.addFlashAttribute("success", "Evaluación creada exitosamente");
            return "redirect:/admin/evaluacion/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la evaluación: " + e.getMessage());
            return "redirect:/admin/evaluacion/nuevo";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
                                         RedirectAttributes redirectAttributes) {
        return evaluacionService.buscarPorId(id)
            .map(evaluacion -> {
                model.addAttribute("evaluacion", evaluacion);
                model.addAttribute("alumnos", evaluacionService.listarTodas());
                model.addAttribute("tutores", evaluacionService.listarTutoresPracticas());
                model.addAttribute("capacidades", evaluacionService.listarCapacidadesActivas());
                model.addAttribute("soloLectura", false);
                return "admin/evaluacion/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Evaluación no encontrada");
                return "redirect:/admin/evaluacion/listar";
            });
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id, 
                                @ModelAttribute Evaluacion evaluacion,
                                RedirectAttributes redirectAttributes) {
        try {
            evaluacion.setId(id);
            evaluacionService.guardar(evaluacion);
            redirectAttributes.addFlashAttribute("success", "Evaluación actualizada exitosamente");
            return "redirect:/admin/evaluacion/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la evaluación: " + e.getMessage());
            return "redirect:/admin/evaluacion/editar/" + id;
        }
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return evaluacionService.buscarPorId(id)
            .map(evaluacion -> {
                model.addAttribute("evaluacion", evaluacion);
                model.addAttribute("alumnos", evaluacionService.listarTodas());
                model.addAttribute("tutores", evaluacionService.listarTutoresPracticas());
                model.addAttribute("capacidades", evaluacionService.listarCapacidadesActivas());
                model.addAttribute("soloLectura", true);
                return "admin/evaluacion/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Evaluación no encontrada");
                return "redirect:/admin/evaluacion/listar";
            });
    }

    @GetMapping("/eliminar/{id}")
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
        return "redirect:/admin/evaluacion/listar";
    }
}

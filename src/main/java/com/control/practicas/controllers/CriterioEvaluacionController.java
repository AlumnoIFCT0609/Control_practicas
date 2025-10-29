package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.CriterioEvaluacion;
import com.control.practicas.services.CriterioEvaluacionService;

import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion/criterio")
public class CriterioEvaluacionController {

    private final CriterioEvaluacionService criterioService;

    public CriterioEvaluacionController(CriterioEvaluacionService criterioService) {
        this.criterioService = criterioService;
    }

    @GetMapping
    public String listar(Model model) {
        List<CriterioEvaluacion> criterios = criterioService.listarTodos();
        model.addAttribute("criterios", criterios);
        return "admin/evaluacion/criterios";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("criterio", new CriterioEvaluacion());
        model.addAttribute("viewName", "admin/evaluacion/criterios");
        return "layout";

    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return criterioService.buscarPorId(id)
            .map(criterio -> {
                model.addAttribute("criterio", criterio);
                model.addAttribute("viewName", "admin/evaluacion/criterios");
                return "layout";

            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
                return "redirect:/admin/evaluacion";
            });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CriterioEvaluacion criterio, RedirectAttributes redirectAttributes) {
        try {
            criterioService.guardar(criterio);
            redirectAttributes.addFlashAttribute("success", "Criterio guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el criterio: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (criterioService.existePorId(id)) {
                criterioService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Criterio eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el criterio: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return criterioService.buscarPorId(id)
            .map(criterio -> {
                criterio.setActivo(!criterio.getActivo());
                criterioService.guardar(criterio);
                redirectAttributes.addFlashAttribute("success", 
                    criterio.getActivo() ? "Criterio activado" : "Criterio desactivado");
                
                return "redirect:/admin/evaluacion/criterios";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
                return "redirect:/admin/evaluacion";
            });
    }
}

package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.CriterioEvaluacion;
import com.control.practicas.services.CriterioEvaluacionService;

import java.util.List;

@Controller
@RequestMapping("/admin/evaluacion/criterios")
public class CriterioEvaluacionController {

    private final CriterioEvaluacionService criterioEvaluacionService;

    public CriterioEvaluacionController(CriterioEvaluacionService criterioEvaluacionService) {
        this.criterioEvaluacionService = criterioEvaluacionService;
    }

    @GetMapping
    public String listar(Model model) {
        List<CriterioEvaluacion> criterios = criterioEvaluacionService.listarTodos();
        model.addAttribute("criterios", criterios);
        model.addAttribute("soloLectura", false); // <-- Añade esto
        model.addAttribute("viewName", "admin/evaluacion/criterios-lista");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("criterio", new CriterioEvaluacion());
        model.addAttribute("viewName", "admin/evaluacion/criterios-form");
        return "layout";

    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return criterioEvaluacionService.buscarPorId(id)
            .map(criterio -> {
                // LOGS DE DEPURACIÓN
                System.out.println("=== CARGAR FORMULARIO EDITAR ===");
                System.out.println("ID recibido en URL: " + id);
                System.out.println("ID del objeto criterio: " + criterio.getId());
                System.out.println("Nombre: " + criterio.getNombre());
                System.out.println("================================");
                
                model.addAttribute("criterio", criterio);
                model.addAttribute("viewName", "admin/evaluacion/criterios-form");
                return "layout";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
                return "redirect:/admin/evaluacion/criterios";
            });
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CriterioEvaluacion criterio, RedirectAttributes redirectAttributes) {
        try {
        	 // LOGS DE DEPURACIÓN
            System.out.println("=== DEBUG GUARDAR ===");
            System.out.println("ID: " + criterio.getId());
            System.out.println("Nombre: " + criterio.getNombre());
            System.out.println("Peso: " + criterio.getPeso());
            System.out.println("====================");
        	
        	
        	
            if (criterio.getId() != null) {
                // Verificar que existe
                CriterioEvaluacion existente = criterioEvaluacionService.buscarPorId(criterio.getId())
                                            .orElseThrow(() -> new IllegalArgumentException("Criterio no encontrado"));
                
                // Actualizar los campos del existente
                existente.setNombre(criterio.getNombre());
                existente.setPeso(criterio.getPeso());
                existente.setDescripcion(criterio.getDescripcion());
                existente.setActivo(criterio.getActivo());
                
                // Guardar el objeto existente (no el nuevo)
                criterioEvaluacionService.guardar(existente);
            } else {
                // Para nuevos registros, guardar directamente
                criterioEvaluacionService.guardar(criterio);
            }
            redirectAttributes.addFlashAttribute("success", "Criterio guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el criterio: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion/criterios";
    }
        @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (criterioEvaluacionService.existePorId(id)) {
                criterioEvaluacionService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Criterio eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el criterio: " + e.getMessage());
        }
        return "redirect:/admin/evaluacion/criterios";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return criterioEvaluacionService.buscarPorId(id)
            .map(criterio -> {
                criterio.setActivo(!criterio.getActivo());
                criterioEvaluacionService.guardar(criterio);
                redirectAttributes.addFlashAttribute("success", 
                    criterio.getActivo() ? "Criterio activado" : "Criterio desactivado");
                
                return "redirect:/admin/evaluacion/criterios";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Criterio no encontrado");
                return "redirect:/admin/evaluacion/criterios";
            });
    }
}

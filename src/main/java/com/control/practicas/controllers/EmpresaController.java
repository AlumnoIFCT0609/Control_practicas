package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.*;
import com.control.practicas.repositories.*;
import com.control.practicas.services.EmpresaService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {
    
    private final EmpresaRepository empresaRepository;
    private final EmpresaService empresaService;
    
    public EmpresaController(EmpresaRepository empresaRepository,
                            EmpresaService empresaService) {
        this.empresaRepository = empresaRepository;
        this.empresaService = empresaService;
    }
    
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Empresa> empresas = empresaService.listarTodas();
        model.addAttribute("empresas", empresas);
        model.addAttribute("viewName", "empresa/listar");
        return "layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("empresa", new Empresa());
        model.addAttribute("viewName", "empresa/form");
        return "layout";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Empresa> empresaOpt = empresaService.buscarPorId(id);
        
        if (empresaOpt.isPresent()) {
            model.addAttribute("empresa", empresaOpt.get());
            model.addAttribute("viewName", "empresa/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Empresa no encontrada");
            return "redirect:/empresa/listar";
        }
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empresa empresa, RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre de la empresa es obligatorio");
                return "redirect:/empresa/nuevo";
            }
            
            if (empresa.getCif() == null || empresa.getCif().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El CIF es obligatorio");
                return "redirect:/empresa/nuevo";
            }
            
            // Verificar si el CIF ya existe (excepto si es la misma empresa)
            Optional<Empresa> empresaExistente = empresaRepository.findByCif(empresa.getCif());
            if (empresaExistente.isPresent() && !empresaExistente.get().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una empresa con ese CIF");
                return empresa.getId() == null ? "redirect:/empresa/nuevo" : "redirect:/empresa/editar/" + empresa.getId();
            }
            
            // Verificar si el email ya existe (excepto si es la misma empresa)
            if (empresa.getEmail() != null && !empresa.getEmail().trim().isEmpty()) {
                Optional<Empresa> empresaExistenteEmail = empresaRepository.findByEmail(empresa.getEmail());
                if (empresaExistenteEmail.isPresent() && !empresaExistenteEmail.get().getId().equals(empresa.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe una empresa con ese email");
                    return empresa.getId() == null ? "redirect:/empresa/nuevo" : "redirect:/empresa/editar/" + empresa.getId();
                }
            }
            
            empresaService.guardar(empresa);
            redirectAttributes.addFlashAttribute("success", 
                empresa.getId() == null ? "Empresa creada exitosamente" : "Empresa actualizada exitosamente");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/empresa/listar";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Empresa> empresaOpt = empresaService.buscarPorId(id);
            if (empresaOpt.isPresent()) {
                Empresa empresa = empresaOpt.get();
                
                // Verificar si tiene alumnos o tutores asociados
                if (!empresa.getAlumnos().isEmpty() || !empresa.getTutoresPracticas().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "No se puede eliminar la empresa porque tiene alumnos o tutores asociados. Desactívela en su lugar.");
                    return "redirect:/empresa/listar";
                }
                
                empresaService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Empresa eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Empresa no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la empresa: " + e.getMessage());
        }
        return "redirect:/empresa/listar";
    }
    
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Empresa> empresaOpt = empresaService.buscarPorId(id);
            if (empresaOpt.isPresent()) {
                Empresa empresa = empresaOpt.get();
                empresa.setActiva(!empresa.getActiva());
                empresaService.guardar(empresa);
                redirectAttributes.addFlashAttribute("success", 
                    empresa.getActiva() ? "Empresa activada exitosamente" : "Empresa desactivada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Empresa no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado de la empresa: " + e.getMessage());
        }
        return "redirect:/empresa/listar";
    }
}
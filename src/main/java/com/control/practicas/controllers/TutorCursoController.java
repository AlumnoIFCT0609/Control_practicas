package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.*;
import com.control.practicas.repositories.*;
import com.control.practicas.services.TutorCursoService;
import com.control.practicas.services.UsuarioService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tutorcurso")
public class TutorCursoController {
    
    private final TutorCursoRepository tutorCursoRepository;
    private final TutorCursoService tutorCursoService;


    
    public TutorCursoController(TutorCursoRepository tutorCursoRepository,
                                TutorCursoService tutorCursoService) {
        this.tutorCursoRepository = tutorCursoRepository;
        this.tutorCursoService = tutorCursoService;


    }
    /* @GetMapping("/tutorcurso/dashboard")
    public String dashboard(Model model) {
        List<TutorCurso> tutores = tutorCursoService.listarTodos();
        model.addAttribute("tutores", tutores);
        model.addAttribute("viewName", "tutorcurso/dashboard");
        return "layout";
    } 
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
    	List<TutorCurso> tutores = tutorCursoService.listarTodos();
        model.addAttribute("pageTitle", "Dashboard - Administrador");
        //model.addAttribute("viewName", "tutorcurso/dashboard");
        model.addAttribute("content", "tutorcurso/dashboard");
        return "layout";
    }*/
    
    @GetMapping("/listar")
    public String listar(Model model) {
        List<TutorCurso> tutores = tutorCursoService.listarTodos();
        model.addAttribute("tutores", tutores);
        model.addAttribute("viewName", "admin/tutorcurso/listar");
        return "layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("tutorCurso", new TutorCurso());
        model.addAttribute("viewName", "admin/tutorcurso/form");
        return "layout";
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TutorCurso> tutorOpt = tutorCursoService.buscarPorId(id);
        
        if (tutorOpt.isPresent()) {
            model.addAttribute("tutorCurso", tutorOpt.get());
            model.addAttribute("viewName", "admin/tutorcurso/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tutor de Curso no encontrado");
            return "redirect:/admin/tutorcurso/listar";
        }
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute TutorCurso tutorCurso, RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
            if (tutorCurso.getNombre() == null || tutorCurso.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio");
                return "redirect:/admin/tutorcurso/nuevo";
            }
            
            if (tutorCurso.getApellidos() == null || tutorCurso.getApellidos().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Los apellidos son obligatorios");
                return "redirect:/admin/tutorcurso/nuevo";
            }
            
            if (tutorCurso.getDni() == null || tutorCurso.getDni().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DNI es obligatorio");
                return "redirect:/admin/tutorcurso/nuevo";
            }
            
            // Verificar si el DNI ya existe (excepto si es el mismo tutor)
            Optional<TutorCurso> tutorExistente = tutorCursoRepository.findByDni(tutorCurso.getDni());
            if (tutorExistente.isPresent() && !tutorExistente.get().getId().equals(tutorCurso.getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un tutor con ese DNI");
                return tutorCurso.getId() == null ? "redirect:/admin/tutorcurso/nuevo" : "redirect:/admin/tutorcurso/editar/" + tutorCurso.getId();
            }
            
            // Verificar si el email ya existe (excepto si es el mismo tutor)
            if (tutorCurso.getEmail() != null && !tutorCurso.getEmail().trim().isEmpty()) {
                Optional<TutorCurso> tutorExistenteEmail = tutorCursoRepository.findByEmail(tutorCurso.getEmail());
                if (tutorExistenteEmail.isPresent() && !tutorExistenteEmail.get().getId().equals(tutorCurso.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe un tutor con ese email");
                    return tutorCurso.getId() == null ? "redirect:/admin/tutorcurso/nuevo" : "redirect:/admin/tutorcurso/editar/" + tutorCurso.getId();
                }
            }
            
            tutorCursoService.guardar(tutorCurso);
            redirectAttributes.addFlashAttribute("success", 
                tutorCurso.getId() == null ? "Tutor de Curso creado exitosamente" : "Tutor de Curso actualizado exitosamente");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el tutor: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/tutorcurso/listar";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<TutorCurso> tutorOpt = tutorCursoService.buscarPorId(id);
            if (tutorOpt.isPresent()) {
                TutorCurso tutor = tutorOpt.get();
                
                // Verificar si tiene cursos asociados
                if (!tutor.getCursos().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "No se puede eliminar el tutor porque tiene cursos asociados. Desactívelo en su lugar.");
                    return "redirect:/admin/tutorcurso/listar";
                }
                
                tutorCursoService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Tutor de Curso eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tutor de Curso no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el tutor: " + e.getMessage());
        }
        return "redirect:/admin/tutorcurso/listar";
    }
    
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<TutorCurso> tutorOpt = tutorCursoService.buscarPorId(id);
            if (tutorOpt.isPresent()) {
                TutorCurso tutor = tutorOpt.get();
                tutor.setActivo(!tutor.getActivo());
                tutorCursoService.guardar(tutor);
                redirectAttributes.addFlashAttribute("success", 
                    tutor.getActivo() ? "Tutor activado exitosamente" : "Tutor desactivado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tutor de Curso no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del tutor: " + e.getMessage());
        }
        return "redirect:/admin/tutorcurso/listar";
    }
    @GetMapping("/crear-usuario/{id}")
    public String crearUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = tutorCursoService.crearUsuarioParaTutorCurso(id);
            redirectAttributes.addFlashAttribute("success", 
                "Usuario creado exitosamente. Email: " + usuario.getEmail() + 
                " | Password inicial: DNI del alumno");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/admin/tutorcurso/listar";
    }
    
    
}
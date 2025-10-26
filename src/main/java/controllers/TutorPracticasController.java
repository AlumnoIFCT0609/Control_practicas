package controllers;

import models.*;
import repositories.*;
import services.TutorPracticasService;

//import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin/tutorpracticas")
public class TutorPracticasController {

    private final TutorPracticasRepository tutorPracticasRepository;
    private final TutorPracticasService tutorPracticasService;
    private final EmpresaRepository empresaRepository; 

    public TutorPracticasController(TutorPracticasRepository tutorPracticasRepository,
            TutorPracticasService tutorPracticasService,
            EmpresaRepository empresaRepository) { 
    		this.tutorPracticasRepository = tutorPracticasRepository;
    		this.tutorPracticasService = tutorPracticasService;
    		this.empresaRepository = empresaRepository; 
    }
    @GetMapping("/listar")
    public String listar(Model model) {
        List<TutorPracticas> tutores = tutorPracticasService.listarTodos();
        model.addAttribute("tutores", tutores);
        model.addAttribute("viewName", "admin/tutorpracticas/listar");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("tutorPracticas", new TutorPracticas());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("viewName", "admin/tutorpracticas/form");
        return "layout";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TutorPracticas> tutorOpt = tutorPracticasService.buscarPorId(id);
        if (tutorOpt.isPresent()) {
            model.addAttribute("tutorPracticas", tutorOpt.get());
            model.addAttribute("empresas", empresaRepository.findAll());
            model.addAttribute("viewName", "admin/tutorpracticas/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Tutor de prácticas no encontrado");
            return "redirect:/admin/tutorpracticas/listar";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute TutorPracticas tutorPracticas, RedirectAttributes redirectAttributes) {
        try {
            // Validaciones básicas
        	 if (tutorPracticas.getId() != null) {
                 Optional<TutorPracticas> existenteOpt = tutorPracticasService.buscarPorId(tutorPracticas.getId());
                 if (existenteOpt.isPresent()) {
                     // conservar la fecha de creación original
                     tutorPracticas.setFechaCreacion(existenteOpt.get().getFechaCreacion());
                 } else {
                     redirectAttributes.addFlashAttribute("error", "No se encontró el tutor a editar");
                     return "redirect:/admin/tutorpracticas/listar";
                 }
             } else {
                 // nuevo: fijar fecha de creación si hace falta
                 tutorPracticas.setFechaCreacion(LocalDateTime.now());
             }
        	
            if (tutorPracticas.getNombre() == null || tutorPracticas.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio");
                return "redirect:/admin/tutorpracticas/nuevo";
            }

            if (tutorPracticas.getApellidos() == null || tutorPracticas.getApellidos().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Los apellidos son obligatorios");
                return "redirect:/admin/tutorpracticas/nuevo";
            }

            if (tutorPracticas.getDni() == null || tutorPracticas.getDni().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El DNI es obligatorio");
                return "redirect:/admin/tutorpracticas/nuevo";
            }

            // Verificar si el DNI ya existe
            Optional<TutorPracticas> tutorExistente = tutorPracticasRepository.findByDni(tutorPracticas.getDni());
            if (tutorExistente.isPresent() && !tutorExistente.get().getId().equals(tutorPracticas.getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un tutor de prácticas con ese DNI");
                return tutorPracticas.getId() == null ? "redirect:/admin/tutorpracticas/nuevo" : "redirect:/admin/tutorpracticas/editar/" + tutorPracticas.getId();
            }

            // Verificar si el email ya existe
            if (tutorPracticas.getEmail() != null && !tutorPracticas.getEmail().trim().isEmpty()) {
                Optional<TutorPracticas> tutorExistenteEmail = tutorPracticasRepository.findByEmail(tutorPracticas.getEmail());
                if (tutorExistenteEmail.isPresent() && !tutorExistenteEmail.get().getId().equals(tutorPracticas.getId())) {
                    redirectAttributes.addFlashAttribute("error", "Ya existe un tutor de prácticas con ese email");
                    return tutorPracticas.getId() == null ? "redirect:/admin/tutorpracticas/nuevo" : "redirect:/admin/tutorpracticas/editar/" + tutorPracticas.getId();
                }
            }

            tutorPracticasService.guardar(tutorPracticas);
            redirectAttributes.addFlashAttribute("success",
                    tutorPracticas.getId() == null ? "Tutor de prácticas creado exitosamente" : "Tutor de prácticas actualizado exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el tutor: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/tutorpracticas/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<TutorPracticas> tutorOpt = tutorPracticasService.buscarPorId(id);
            if (tutorOpt.isPresent()) {
                tutorPracticasService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Tutor de prácticas eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tutor de prácticas no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el tutor de prácticas: " + e.getMessage());
        }
        return "redirect:/admin/tutorpracticas/listar";
    }

    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<TutorPracticas> tutorOpt = tutorPracticasService.buscarPorId(id);
            if (tutorOpt.isPresent()) {
                TutorPracticas tutor = tutorOpt.get();
                tutor.setActivo(!tutor.getActivo());
                tutorPracticasService.guardar(tutor);
                redirectAttributes.addFlashAttribute("success",
                        tutor.getActivo() ? "Tutor activado exitosamente" : "Tutor desactivado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Tutor de prácticas no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del tutor: " + e.getMessage());
        }
        return "redirect:/admin/tutorpracticas/listar";
    }
    @GetMapping("/crear-usuario/{id}")
    public String crearUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = tutorPracticasService.crearUsuarioParaTutorPracticas(id);
            redirectAttributes.addFlashAttribute("success", 
                "Usuario creado exitosamente. Email: " + usuario.getEmail() + 
                " | Password inicial: DNI del alumno");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/admin/tutorpracticas/listar";
    }
    
}


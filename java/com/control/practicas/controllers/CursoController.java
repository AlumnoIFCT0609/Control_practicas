package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.repositories.TutorCursoRepository;
import com.control.practicas.services.CursoService;
import com.control.practicas.services.TutorCursoService;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/curso")
public class CursoController {

    private final CursoService cursoService;
    private final TutorCursoRepository tutorCursoRepository;
    private final TutorCursoService tutorCursoService;
    public CursoController(CursoService cursoService, TutorCursoRepository tutorCursoRepository, TutorCursoService tutorCursoService) {
        this.cursoService = cursoService;
        this.tutorCursoRepository = tutorCursoRepository;
        this.tutorCursoService=tutorCursoService;
    }
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDate.parse(text));
                }
            }
        });
    }

    // Listar todos los cursos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        model.addAttribute("cursos", cursos);
        model.addAttribute("viewName", "admin/curso/listar");
        return "layout";
    }

    // Mostrar formulario para crear nuevo curso
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Curso curso = new Curso();
        curso.setActivo(true); // Por defecto activo
        curso.setTutorCursoId(null); // Para inicializar el campo transitorio
        model.addAttribute("curso", curso);
        model.addAttribute("tutores", tutorCursoRepository.findAll());
        model.addAttribute("viewName", "admin/curso/form");
        return "layout";
    }

    // Mostrar formulario para editar curso existente
 // Mostrar formulario para editar curso existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 1) Cargar primero las listas para los selects
            List<TutorCurso> tutores = tutorCursoService.listarTodos();

            // 2) Cargar el curso
            Curso curso = cursoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            // 3) Asegurar inicialización de la relación (evitar LazyInitializationException)
            if (curso.getTutorCurso() != null) {
                curso.getTutorCurso().getId();
                // 4) Copiar a campo transitorio para que el form lo muestre/seleccione correctamente
                curso.setTutorCursoId(curso.getTutorCurso().getId());
            } else {
                curso.setTutorCursoId(null);
            }
            // Debug temporal
            System.out.println("DEBUG curso.id=" + curso.getId() +
                    " tutorId=" + (curso.getTutorCurso() != null ? curso.getTutorCurso().getId() : "null"));
            tutores.forEach(t -> System.out.println("DEBUG tutor id=" + t.getId() + " nombre=" + t.getNombre() + " apellidos=" + t.getApellidos()));
            if (curso.getTutorCurso() != null) {
                curso.setTutorCursoId(curso.getTutorCurso().getId());
            } else {
                curso.setTutorCursoId(null);
            }
            model.addAttribute("curso", curso);
            model.addAttribute("tutores", tutores);
            model.addAttribute("viewName", "admin/curso/form");
            return "layout";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar el curso: " + e.getMessage());
            return "redirect:/admin/curso/listar";
        }
    }

    // Guardar o actualizar curso
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Curso curso, RedirectAttributes redirectAttributes) {
        boolean esNuevo = (curso.getId() == null);
        try {
            // Usar el campo transitorio tutorCursoId enviado desde el form
            /*if (curso.getTutorCursoId() != null && curso.getTutorCursoId() > 0) {
                Long tutorId = curso.getTutorCursoId();
                TutorCurso tutor = tutorCursoRepository.findById(tutorId)
                        .orElseThrow(() -> new RuntimeException("TutorCurso no encontrado"));
                curso.setTutorCurso(tutor);
            } else {
                curso.setTutorCurso(null);
            }*/
        	if (curso.getTutorCursoId() != null && curso.getTutorCursoId() > 0) {
        	    TutorCurso tutor = tutorCursoRepository.findById(curso.getTutorCursoId())
        	        .orElseThrow(() -> new RuntimeException("TutorCurso no encontrado"));
        	    curso.setTutorCurso(tutor);
        	} else {
        	    curso.setTutorCurso(null);
        	}


            // Persistir (service gestiona repository.save y transacción)
            cursoService.guardar(curso);

            redirectAttributes.addFlashAttribute("success",
                    esNuevo ? "Curso creado exitosamente" : "Curso actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el curso: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/curso/listar";
    }


    // Eliminar curso
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (cursoService.existePorId(id)) {
                cursoService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Curso eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el curso: " + e.getMessage());
        }
        return "redirect:/admin/curso/listar";
    }
    
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Curso> cursoOpt = cursoService.buscarPorId(id);
            if (cursoOpt.isPresent()) {
                Curso curso = cursoOpt.get();
                curso.setActivo(!curso.getActivo());
                cursoService.guardar(curso);
                redirectAttributes.addFlashAttribute("success", 
                    curso.getActivo() ? "Curso activado" : "Curso desactivado");
            } else {
                redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar el estado del curso: " + e.getMessage());
        }
        return "redirect:/admin/curso/listar";
    }
    
    
    
}
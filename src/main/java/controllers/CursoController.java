package controllers;

import models.Curso;
import repositories.TutorCursoRepository;
import services.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/curso")
public class CursoController {

    private final CursoService cursoService;
    private final TutorCursoRepository tutorCursoRepository;

    public CursoController(CursoService cursoService, TutorCursoRepository tutorCursoRepository) {
        this.cursoService = cursoService;
        this.tutorCursoRepository = tutorCursoRepository;
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
        model.addAttribute("curso", curso);
        model.addAttribute("tutores", tutorCursoRepository.findAll());
        model.addAttribute("viewName", "admin/curso/form");
        return "layout";
    }

    // Mostrar formulario para editar curso existente
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOpt = cursoService.buscarPorId(id);
        
        if (cursoOpt.isPresent()) {
            model.addAttribute("curso", cursoOpt.get());
            model.addAttribute("tutores", tutorCursoRepository.findAll());
            model.addAttribute("viewName", "admin/curso/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            return "redirect:/admin/curso/listar";
        }
    }

    // Guardar o actualizar curso
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Curso curso, RedirectAttributes redirectAttributes) {
        try {
            // Si es nuevo, establecer fecha de creación
            if (curso.getId() == null) {
                curso.setFechaCreacion();
            }
            
            cursoService.guardar(curso);
            redirectAttributes.addFlashAttribute("success", 
                curso.getId() == null ? "Curso creado exitosamente" : "Curso actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el curso: " + e.getMessage());
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
}
package controllers;

import services.CursoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import models.Curso;

//import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    
    private final CursoService cursoService;
    
    public AdminController(CursoService cursoService) {
    	 this.cursoService = cursoService;
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        
        // Crear un mapa con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }
        
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("viewName", "admin/dashboard");
        return "layout";
    }
    /*
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("alumnos", alumnoRepository.findAll());
        model.addAttribute("pageTitle", "Dashboard - Administrador");
        model.addAttribute("viewName", "admin/dashboard");
        //model.addAttribute("content", "admin/dashboard");
        return "layout";
    }
    */
}
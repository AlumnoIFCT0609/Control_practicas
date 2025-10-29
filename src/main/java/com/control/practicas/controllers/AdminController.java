package com.control.practicas.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.CursoService;
import com.control.practicas.services.EmpresaService;
import com.control.practicas.services.TutorCursoService;
import com.control.practicas.services.TutorPracticasService;

//import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final TutorCursoService tutorCursoService;
    private final AlumnoService alumnoService;
    public AdminController(CursoService cursoService, EmpresaService empresaService, 
    						TutorPracticasService tutorPracticasService,
    						TutorCursoService tutorCursoService,
    						AlumnoService alumnoService){
    	 this.cursoService = cursoService;
    	 this.empresaService= empresaService;
    	 this.tutorPracticasService=tutorPracticasService;
    	 this.alumnoService=alumnoService;
    	 this.tutorCursoService=tutorCursoService;
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        List<Empresa> empresas = empresaService.listarTodas(); 
        List<TutorPracticas> tutorp = tutorPracticasService.listarTodos();
        List<Alumno> alumnos = alumnoService.listarTodos();
        // Crear un mapa con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }
        
        model.addAttribute("cursos", cursos);
        long cursosActivos = cursos.stream()
                .filter(c -> c.isActivo())
                .count();
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorp", tutorp);
        model.addAttribute("alumnos",alumnos);
  
        model.addAttribute("viewName", "admin/dashboard");
        return "layout";
    }
    @GetMapping("/reportes/report")
    public String reports(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        List<Empresa> empresas = empresaService.listarTodas();
        List<TutorPracticas> tutoresPracticas = tutorPracticasService.listarTodos();
        List<TutorCurso> tutoresCurso = tutorCursoService.listarTodos(); // ← AGREGAR ESTO
                

        List<Alumno> alumnos = alumnoService.listarTodos();

        // Crear un mapa con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }

        // Calcular estadísticas de cursos
        long cursosActivos = cursos.stream().filter(Curso::isActivo).count();
        long cursosFinalizados = cursos.stream().filter(c -> !c.isActivo()).count();
        
        // Calcular estadísticas de empresas
        long empresasActivas = empresas.stream().filter(Empresa::getActiva).count();
        long empresasInactivas = empresas.stream().filter(e -> !e.getActiva()).count();
        
        // Calcular estadísticas de alumnos
        long alumnosEnPracticas = alumnos.stream().filter(Alumno::isActivo).count();
        long alumnosFinalizados = alumnos.stream().filter(a -> !a.isActivo()).count();

        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutoresPracticas", tutoresPracticas);
        model.addAttribute("tutoresCurso", tutoresCurso);
        model.addAttribute("alumnos", alumnos);
        
        // Agregar estadísticas calculadas
        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("cursosFinalizados", cursosFinalizados);
        model.addAttribute("empresasActivas", empresasActivas);
        model.addAttribute("empresasInactivas", empresasInactivas);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);

        model.addAttribute("viewName", "admin/reportes/report");
        return "layout";
    }   
    
    
    
    
}
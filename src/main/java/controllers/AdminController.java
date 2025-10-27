package controllers;

import services.AlumnoService;
import services.CursoService;
import services.EmpresaService;
import services.TutorPracticasService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import models.Alumno;
import models.Curso;
import models.Empresa;
import models.TutorPracticas;

//import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final AlumnoService alumnoService;
    public AdminController(CursoService cursoService, EmpresaService empresaService, 
    						TutorPracticasService tutorPracticasService,
    						AlumnoService alumnoService){
    	 this.cursoService = cursoService;
    	 this.empresaService= empresaService;
    	 this.tutorPracticasService=tutorPracticasService;
    	 this.alumnoService=alumnoService;
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
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorp", tutorp);
        model.addAttribute("alumnos",alumnos);
  
        model.addAttribute("viewName", "admin/dashboard");
        return "layout";
    }
  
}
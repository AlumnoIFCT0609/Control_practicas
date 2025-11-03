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
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorp", tutorp);
        model.addAttribute("alumnos",alumnos);
  
        model.addAttribute("viewName", "admin/dashboard");
        return "layout";
    }
 
  
    
    
    @GetMapping("/reportes/report")
    public String reports(Model model) {
    	//  se enlistan los cursos existentes cursos 
    	List<Curso> cursos = cursoService.listarTodos();
    	//  se enlistan las empresas
        List<Empresa> empresas = empresaService.listarTodas();
        //  se enlistan los tutores del curso
        List<TutorCurso> tutorC = tutorCursoService.listarTodos();
        //  y los tutores de prácticas
        List<TutorPracticas> tutorP = tutorPracticasService.listarTodos();    
        // y, los alumnos también
        List<Alumno> alumnos = alumnoService.listarTodos();

        // Creamos un mapeo con el número de alumnos por curso
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
        // Calculo estadísticas de alumnos
        long alumnosEnPracticas = alumnos.stream().filter(Alumno::isActivo).count();
        long alumnosFinalizados = alumnos.stream().filter(a -> !a.isActivo()).count();
        //Calculo estadisticas de Tutores
        long tutorCurso = tutorC.stream().filter(TutorCurso::getActivo).count();
        long tutorCursoInac = tutorC.stream().filter(tc ->!tc.getActivo()).count();
        long tutorPracticas = tutorP.stream().filter(TutorPracticas::getActivo).count();
        long tutorPracticasInac = tutorP.stream().filter(tp ->!tp.getActivo()).count();
        
        //testeo de paso de variables 
        System.out.println("========================");
        System.out.println("Todos los tutores de Curso: "+ tutorC.size());
        System.out.println("Todas las empresas de Curso: "+ empresas.size());
        System.out.println("========================");
        
        //Campos del modelo
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorP", tutorP.size());
        model.addAttribute("tutorC", tutorC.size());
        model.addAttribute("alumnos", alumnos);
        
        // Campos calculados
        model.addAttribute("tutorCurso",tutorCurso);
        model.addAttribute("tutorCursoInac",tutorCursoInac);
        model.addAttribute("tutorPracticas",tutorPracticas);
        model.addAttribute("tutorPracticasInac",tutorPracticasInac);
        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("cursosFinalizados", cursosFinalizados);
        model.addAttribute("empresasActivas", empresasActivas);
        model.addAttribute("empresasInactivas", empresasInactivas);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);

        
        //cargamos la vista y enviamos la plantilla
        model.addAttribute("viewName", "admin/reportes/report");
        return "layout";
    }   
    
    
    
    
    
}
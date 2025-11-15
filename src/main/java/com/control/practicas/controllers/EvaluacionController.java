package com.control.practicas.controllers;

import com.control.practicas.dto.TutorPracticasDTO;
import com.control.practicas.models.*;
import com.control.practicas.services.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("admin/evaluaciones")
public class EvaluacionController {
    
    private final EvaluacionService evaluacionService;
    private final CapacidadEvaluacionService capacidadService;
    private final CriterioEvaluacionService criterioService;
    private final TutorCursoService tutorCursoService;
    private final TutorPracticasService tutorPracticasService;
    private final AlumnoService alumnoService;
    private final TutorPracticasService tutorService;
    private final CursoService cursoService;
    
    public EvaluacionController(
            EvaluacionService evaluacionService,
            TutorCursoService tutorCursoService,
            CursoService cursoService,
            TutorPracticasService tutorPracticasService,
            CapacidadEvaluacionService capacidadService,
            CriterioEvaluacionService criterioService,
            AlumnoService alumnoService,
            TutorPracticasService tutorService) {
        this.evaluacionService = evaluacionService;
        this.tutorCursoService = tutorCursoService;
        this.capacidadService = capacidadService;
        this.criterioService = criterioService;
        this.alumnoService = alumnoService;
        this.tutorPracticasService = tutorPracticasService;
        this.tutorService = tutorService;
        this.cursoService = cursoService; 
    }
    
    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        List<Evaluacion> evaluaciones;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
            Alumno alumno = alumnoService.obtenerPorEmail(authentication.getName());
            evaluaciones = evaluacionService.buscarPorAlumno(alumno.getId());
            model.addAttribute("alumno", alumno);
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            Optional<TutorCurso> tutorCurso = tutorCursoService.buscarPorEmail(authentication.getName());
            evaluaciones = evaluacionService.buscarPorTutor(tutorCurso.get().getId());
            
            List<Alumno> alumnos = evaluaciones.stream()
                .map(Evaluacion::getAlumno)
                .distinct()
                .collect(Collectors.toList());
            
            model.addAttribute("tutorCurso", tutorCurso);
            model.addAttribute("alumnos", alumnos);
            
            
            }else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_PRACTICAS"))) {  
        
        	Optional<TutorPracticas> tutorPracticas = tutorPracticasService.buscarPorEmail(authentication.getName());
            
            evaluaciones = evaluacionService.buscarPorTutorPracticas(tutorPracticas.get().getId());
            
            List<Alumno> alumnos = evaluaciones.stream()
                .map(Evaluacion::getAlumno)
                .distinct()
                .collect(Collectors.toList());
            
            model.addAttribute("tutorPracticas", tutorPracticas);
            model.addAttribute("alumnos", alumnos);
        
        
        
            } else {
            evaluaciones = evaluacionService.listarTodas();
        }

        // Sumar puntos obtenidos y máximos
        BigDecimal totalObtenido = evaluaciones.stream()
        	    .map(Evaluacion::getPuntuacion)
        	    .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMaximo = evaluaciones.stream()
        	    .map(e -> BigDecimal.valueOf(e.getCapacidad().getPuntuacionMaxima()))
        	    .reduce(BigDecimal.ZERO, BigDecimal::add);

        	
        BigDecimal notaTotal = evaluaciones.stream()
                .map(e -> {
                    BigDecimal fraccion = e.getPuntuacion()
                            .divide(BigDecimal.valueOf(e.getCapacidad().getPuntuacionMaxima()), 4, RoundingMode.HALF_UP);
                    BigDecimal peso = BigDecimal.valueOf(e.getCapacidad().getCriterio().getPeso());
                    return fraccion.multiply(peso);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        System.out.println("===============================================================================================");
        
        System.out.println("========   " + notaTotal.toString() + "=====================================================");
        
        System.out.println("===============================================================================================");
        notaTotal = notaTotal.setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("totalObtenido", totalObtenido);
        model.addAttribute("totalMaximo", totalMaximo);
        model.addAttribute("notaTotal", notaTotal);
        model.addAttribute("evaluaciones", evaluaciones);

        model.addAttribute("titulo", "Listado de Evaluaciones");
        model.addAttribute("viewName", "admin/evaluacion/listar");
        return "layout";
    }

    
/*    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        List<Evaluacion> evaluaciones;
        // Si el usuario autenticado tiene el rol "ALUMNO"
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {

            String username = authentication.getName(); // nombre del usuario conectado

            // Obtener el alumno asociado al usuario (ajusta según tu modelo)
            Alumno alumno = alumnoService.obtenerPorEmail(username);
            // Filtrar solo sus evaluaciones
            evaluaciones = evaluacionService.buscarPorAlumno(alumno.getId());
            	
          } else {
            // Si es ADMIN o TUTOR, listar todas
            evaluaciones = evaluacionService.listarTodas();
        }

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("titulo", "Listado de Evaluaciones");
        model.addAttribute("viewName", "admin/evaluacion/listar");
        return "layout";
    }*/

    @GetMapping("/nueva")
    public String nuevaEvaluacion(Model model, Authentication authentication) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setFecha(LocalDate.now());

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("titulo", "Nueva Evaluación");
        model.addAttribute("accion", "nueva");

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            Optional<TutorCurso> tutorCurso = tutorCursoService.buscarPorEmail(authentication.getName());

            // Obtener todos los cursos del tutor
            List<Curso> cursos = cursoService.listarPorTutorCurso(tutorCurso.get().getId());

            // Obtener todos los alumnos de esos cursos
            List<Alumno> alumnos = new ArrayList<>();
            for (Curso curso : cursos) {
                alumnos.addAll(alumnoService.listarPorCurso(curso.getId()));
            }
            
            // Eliminar duplicados
            alumnos = alumnos.stream().distinct().collect(Collectors.toList());

            List<TutorPracticas> tutores = alumnos.stream()
                .map(Alumno::getTutorPracticas)
                .filter(t -> t != null)
                .distinct()
                .collect(Collectors.toList());

            model.addAttribute("alumnos", alumnos);
            model.addAttribute("tutores", tutores);

        } else
        	if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_PRACTICAS"))) {
            Optional<TutorPracticas> tutorPracticas = tutorPracticasService.buscarPorEmail(authentication.getName());

            // Obtener alumnos directamente del tutor de prácticas
            List<Alumno> alumnos = alumnoService.listarPorTutorPracticas(tutorPracticas.get().getId());

            List<TutorPracticas> tutores = Arrays.asList(tutorPracticas.get());

            model.addAttribute("alumnos", alumnos);
            model.addAttribute("tutores", tutores);

        } else {
            model.addAttribute("alumnos", alumnoService.listarTodos());
            model.addAttribute("tutores", tutorService.listarTodos());
        }

        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
    }
    
    /*@GetMapping("/nueva")
    public String nuevaEvaluacion(Model model) {
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setFecha(LocalDate.now());
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("titulo", "Nueva Evaluación");
        model.addAttribute("accion", "nueva");
        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
        
    } */
    
   /* @GetMapping("/editar/{id}")
    public String editarEvaluacion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Evaluacion evaluacion = evaluacionService.buscarPorId(id).orElse(null);
        
        if (evaluacion == null) {
            flash.addFlashAttribute("error", "La evaluación no existe");
            return "redirect:/admin/evaluaciones/listar";
        }
        
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("tutores", tutorService.listarTodos());
        model.addAttribute("titulo", "Editar Evaluación");
        model.addAttribute("accion", "editar");
        
        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
    }*/
    
    @GetMapping("/editar/{id}")
    public String editarEvaluacion(@PathVariable Long id, Model model, RedirectAttributes flash, Authentication authentication) {
        Evaluacion evaluacion = evaluacionService.buscarPorId(id).orElse(null);

        if (evaluacion == null) {
            flash.addFlashAttribute("error", "La evaluación no existe");
            return "redirect:/admin/evaluaciones/listar";
        }

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("criterios", criterioService.listarActivos());
        model.addAttribute("capacidades", capacidadService.listarActivas());
        model.addAttribute("titulo", "Editar Evaluación");
        model.addAttribute("accion", "editar");

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            Optional<TutorCurso> tutorCurso = tutorCursoService.buscarPorEmail(authentication.getName());
            
            // Obtener evaluaciones del tutor y extraer alumnos únicos
            List<Evaluacion> evaluaciones = evaluacionService.buscarPorTutor(tutorCurso.get().getId());
            List<Alumno> alumnos = evaluaciones.stream()
                .map(Evaluacion::getAlumno)
                .distinct()
                .collect(Collectors.toList());
            
            // Obtener tutores de prácticas de esos alumnos
            List<TutorPracticas> tutores = alumnos.stream()
                .map(Alumno::getTutorPracticas)
                .filter(t -> t != null)
                .distinct()
                .collect(Collectors.toList());
            
            model.addAttribute("alumnos", alumnos);
            model.addAttribute("tutores", tutores);
            
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_PRACTICAS"))) {
            Optional<TutorPracticas> tutorPracticas = tutorPracticasService.buscarPorEmail(authentication.getName());
            
            // Obtener evaluaciones del tutor y extraer alumnos únicos
            List<Evaluacion> evaluaciones = evaluacionService.buscarPorTutorPracticas(tutorPracticas.get().getId());
            List<Alumno> alumnos = evaluaciones.stream()
                .map(Evaluacion::getAlumno)
                .distinct()
                .collect(Collectors.toList());
            
            // Solo él mismo como tutor
            List<TutorPracticas> tutores = Arrays.asList(tutorPracticas.get());
            
            model.addAttribute("alumnos", alumnos);
            model.addAttribute("tutores", tutores);
            
        } else {
            // ADMIN u otros roles: ver todo
            model.addAttribute("alumnos", alumnoService.listarTodos());
            model.addAttribute("tutores", tutorPracticasService.listarTodos());
        }

        model.addAttribute("viewName", "admin/evaluacion/form");
        return "layout";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Evaluacion evaluacion, RedirectAttributes flash) {
        try {
            evaluacionService.guardar(evaluacion);
            flash.addFlashAttribute("success", "Evaluación guardada correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
        }
        return "redirect:/admin/evaluaciones/listar";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            evaluacionService.eliminar(id);
            flash.addFlashAttribute("success", "Evaluación eliminada correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar la evaluación");
        }
        return "redirect:/admin/evaluaciones/listar";
    }
    
    // Endpoint AJAX para obtener capacidades por criterio
    @GetMapping("/capacidades/{criterioId}")
    @ResponseBody
    public List<CapacidadEvaluacion> obtenerCapacidadesPorCriterio(@PathVariable Long criterioId) {
        CriterioEvaluacion criterio = criterioService.buscarPorId(criterioId).orElse(null);
        if (criterio != null) {
            return capacidadService.listarPorCriterio(criterio);
        }
        return List.of();
    }
    
 // Endpoint AJAX para obtener tutor de prácticas por alumno
    @GetMapping("/tutor-por-alumno/{alumnoId}")
    @ResponseBody
    public TutorPracticasDTO obtenerTutorPorAlumno(@PathVariable Long alumnoId) {
        Alumno alumno = alumnoService.buscarPorId(alumnoId).orElse(null);
        if (alumno == null || alumno.getTutorPracticas() == null) {
            return null;
        }
        
        TutorPracticas tp = alumno.getTutorPracticas();
        return new TutorPracticasDTO(tp.getId(), tp.getNombre() + " " + tp.getApellidos());
    }
    
    
}

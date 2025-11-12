// ============================================
// CONTROLLER - EvaluacionTutorController.java
// ============================================

package com.control.practicas.controllers;

import com.control.practicas.dto.*;
import com.control.practicas.models.*;
import com.control.practicas.services.*;
import com.control.practicas.repositories.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/evaluaciontutor")
public class EvaluacionTutorController {

    private final EvaluacionTutorService evaluacionTutorService;
    private final TutorCursoService tutorCursoService;
    private final TutorPracticasService tutorPracticasService;
    private final AlumnoService alumnoService;
    private final CursoService cursoService; // ✅ NUEVO
    private final YaEvaluadoRepository yaEvaluadoRepository;

    public EvaluacionTutorController(
            EvaluacionTutorService evaluacionTutorService,
            TutorCursoService tutorCursoService,
            YaEvaluadoRepository yaEvaluadoRepository,
            TutorPracticasService tutorPracticasService,
            AlumnoService alumnoService,
            CursoService cursoService) { // ✅ NUEVO
        this.evaluacionTutorService = evaluacionTutorService;
        this.tutorCursoService = tutorCursoService;
        this.tutorPracticasService = tutorPracticasService;
        this.alumnoService = alumnoService;
        this.cursoService = cursoService; // ✅ NUEVO
        this.yaEvaluadoRepository = yaEvaluadoRepository;
    }

    @GetMapping("/listar")
    public String listar(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<EvaluacionTutor> evaluaciones;

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            evaluaciones = evaluacionTutorService.listarTodas();
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            TutorCurso tutor = tutorCursoService.buscarPorEmail(username)
                    .orElseThrow(() -> new RuntimeException("No se encontró el tutor con email: " + username));
            evaluaciones = evaluacionTutorService.listarPorTutorCurso(tutor.getId());
        } else {
            evaluaciones = List.of();
        }

        // Calcular estadísticas
        Double promedioGeneral = null;
        Double mejorPuntuacion = null;
        
        if (!evaluaciones.isEmpty()) {
        	 promedioGeneral = evaluaciones.stream()
                     .map(EvaluacionTutor::getPuntuacion)
                     .filter(p -> p != null)
                     .mapToDouble(BigDecimal::doubleValue)
                     .average()
                     .orElse(0.0);
             
             // Calcular mejor puntuación
             mejorPuntuacion = evaluaciones.stream()
                     .map(EvaluacionTutor::getPuntuacion)
                     .filter(p -> p != null)
                     .mapToDouble(BigDecimal::doubleValue)
                     .max()
                     .orElse(0.0);
        }

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("promedioGeneral", promedioGeneral);
        model.addAttribute("mejorPuntuacion", mejorPuntuacion);
        model.addAttribute("viewName", "evaluaciontutor/listar");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();

        boolean isAlumno = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"));
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
        boolean isTutorCurso = authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"));

        EvaluacionTutor evaluacion = new EvaluacionTutor();
        evaluacion.setFecha(LocalDate.now());

        if (isAlumno) {
            Alumno alumno = alumnoService.obtenerPorEmail(username);
            TutorPracticas tutorPracticas = alumno.getTutorPracticas();
            Empresa empresa = alumno.getEmpresa();
            Curso curso = alumno.getCurso();

            if (yaEvaluadoRepository.existsByAlumnoIdAndTutorPracticasIdAndEmpresaIdAndCursoId(
                    alumno.getId(), tutorPracticas.getId(), empresa.getId(), curso.getId())) {
                redirectAttributes.addFlashAttribute("error", "Ya has realizado tu evaluación.");
                return "redirect:/alumno/dashboard";
            }

            TutorCurso tutorCurso = alumno.getCurso().getTutorCurso();

            evaluacion.setTutorCurso(tutorCurso);
            evaluacion.setTutorPracticas(tutorPracticas);

            model.addAttribute("tutoresCurso", List.of(tutorCurso));
            model.addAttribute("tutoresPracticas", List.of(tutorPracticas));
            model.addAttribute("soloLectura", true);
        } else if (isTutorCurso) {
            Optional<TutorCurso> optTutor = tutorCursoService.buscarPorEmail(username);

            if (optTutor.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No se encontró el tutor de curso asociado.");
                return "redirect:/tutor/dashboard";
            }

            TutorCurso tutor = optTutor.get();
            evaluacion.setTutorCurso(tutor);

            model.addAttribute("tutoresCurso", List.of(tutor));
            
            // Tutores de prácticas se cargarán dinámicamente
            model.addAttribute("tutoresPracticas", List.of());
            model.addAttribute("soloLectura", false);
        } else if (isAdmin) {
            List<TutorCurso> tutoresCurso = tutorCursoService.listarTodos();

            model.addAttribute("tutoresCurso", tutoresCurso);
            model.addAttribute("tutoresPracticas", List.of());
            model.addAttribute("soloLectura", false);
        } else {
            return "null";
        }

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        EvaluacionTutor evaluacion = evaluacionTutorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        boolean soloLectura = !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("tutoresCurso", tutorCursoService.listarTodos());
        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
        model.addAttribute("soloLectura", soloLectura);
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    // ============================================
    // ENDPOINTS AJAX PARA FILTRADO DINÁMICO
    // ============================================
    
    // TutorCurso → TutorPracticas (a través de Curso→Alumno, pero sin exponerlos)
    @GetMapping("/tutores-practicas-por-tutor/{tutorCursoId}")
    @ResponseBody
    public List<TutorPracticasDTO> obtenerTutoresPracticasPorTutorCurso(@PathVariable Long tutorCursoId) {
        TutorCurso tutor = tutorCursoService.buscarPorId(tutorCursoId).orElse(null);
        if (tutor == null) return List.of();
        
        // Obtener tutores de prácticas de los alumnos de los cursos de este tutor
        return tutor.getCursos().stream()
                .flatMap(curso -> curso.getAlumnos().stream())
                .filter(Alumno::isActivo)
                .map(Alumno::getTutorPracticas)
                .filter(tp -> tp != null)
                .distinct()
                .map(tp -> new TutorPracticasDTO(tp.getId(), tp.getNombre() + " " + tp.getApellidos()))
                .collect(Collectors.toList());
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Long tutorCursoId,
                          @RequestParam(required = false) Long tutorPracticasId,
                          @RequestParam(required = false) BigDecimal puntuacion,
                          @RequestParam(required = false) String observaciones,
                          @RequestParam(required = false) String aspectosPositivos,
                          @RequestParam(required = false) String aspectosMejorar,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                          @RequestParam(required = false) Long id,
                          Authentication authentication, 
                          RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        
        try {
            EvaluacionTutor evaluacion;
            if (id != null) {
                evaluacion = evaluacionTutorService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Evaluación no encontrada con ID: " + id));
            } else {
                evaluacion = new EvaluacionTutor();
            }
            
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
                Alumno alumno = alumnoService.obtenerPorEmail(username);
                evaluacion.setTutorCurso(alumno.getCurso().getTutorCurso());
                evaluacion.setTutorPracticas(alumno.getTutorPracticas());
            } else {
                if (tutorCursoId != null) {
                    TutorCurso tutorCurso = tutorCursoService.buscarPorId(tutorCursoId)
                        .orElseThrow(() -> new RuntimeException("Tutor de curso no encontrado con ID: " + tutorCursoId));
                    evaluacion.setTutorCurso(tutorCurso);
                }
                
                if (tutorPracticasId != null) {
                    TutorPracticas tutorPracticas = tutorPracticasService.buscarPorId(tutorPracticasId)
                        .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado con ID: " + tutorPracticasId));
                    evaluacion.setTutorPracticas(tutorPracticas);
                }
            }
            
            evaluacion.setPuntuacion(puntuacion);
            evaluacion.setObservaciones(observaciones);
            evaluacion.setAspectosPositivos(aspectosPositivos);
            evaluacion.setAspectosMejorar(aspectosMejorar);
            evaluacion.setFecha(fecha != null ? fecha : LocalDate.now());
            
            evaluacionTutorService.guardar(evaluacion);
            
            if (id == null) {
                redirectAttributes.addFlashAttribute("success", "Evaluación creada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("success", "Evaluación actualizada correctamente.");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
        }
        
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
            Alumno alumno = alumnoService.obtenerPorEmail(authentication.getName());
            
            YaEvaluado yaEvaluado = new YaEvaluado();
            yaEvaluado.setAlumnoId(alumno.getId());
            yaEvaluado.setCursoId(alumno.getCurso().getId());   
            yaEvaluado.setEmpresaId(alumno.getEmpresa().getId());
            yaEvaluado.setTutorPracticasId(alumno.getTutorPracticas().getId()); 
            yaEvaluado.setFecha(LocalDateTime.now());
            yaEvaluadoRepository.save(yaEvaluado);
            
            return "redirect:/alumno/dashboard";
        }
        return "redirect:/evaluaciontutor/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, 
                           Authentication authentication, 
                           RedirectAttributes redirectAttributes) {
        
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar evaluaciones.");
            return "redirect:/evaluaciontutor/listar";
        }
        
        try {
            evaluacionTutorService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Evaluación eliminada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la evaluación: " + e.getMessage());
        }
        
        return "redirect:/evaluaciontutor/listar";
    }
}

//
//
//package com.control.practicas.controllers;
//
//import com.control.practicas.dto.EvaluacionTutorDTO;
//import com.control.practicas.models.*;
//import com.control.practicas.services.*;
//import com.control.practicas.repositories.*;
//
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/evaluaciontutor")
//public class EvaluacionTutorController {
//
//    private final EvaluacionTutorService evaluacionTutorService;
//    private final TutorCursoService tutorCursoService;
//    private final TutorPracticasService tutorPracticasService;
//    private final AlumnoService alumnoService; // 👈 Necesario para el rol ALUMNO
//    private final YaEvaluadoRepository yaEvaluadoRepository;
//    
//
//    public EvaluacionTutorController(EvaluacionTutorService evaluacionTutorService,
//                                     TutorCursoService tutorCursoService,
//                                     YaEvaluadoRepository yaEvaluadoRepository,
//                                     TutorPracticasService tutorPracticasService,
//                                     
//                                     AlumnoService alumnoService) {
//        this.evaluacionTutorService = evaluacionTutorService;
//        this.tutorCursoService = tutorCursoService;
//        this.tutorPracticasService = tutorPracticasService;
//        this.alumnoService = alumnoService;
//        this.yaEvaluadoRepository = yaEvaluadoRepository;
//       
//    }
//
//   
//    @GetMapping("/listar")
//    public String listar(Model model, Authentication authentication) {
//        String username = authentication.getName();
//        List<EvaluacionTutor> evaluaciones;
//
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
//            evaluaciones = evaluacionTutorService.listarTodas();
//
//        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
//            TutorCurso tutor = tutorCursoService.buscarPorEmail(username)
//                    .orElseThrow(() -> new RuntimeException("No se encontró el tutor con email: " + username));
//            evaluaciones = evaluacionTutorService.listarPorTutorCurso(tutor.getId()); 
//
//        } else {
//            evaluaciones = List.of();
//        }
//
//        model.addAttribute("evaluaciones", evaluaciones);
//        model.addAttribute("viewName", "evaluaciontutor/listar");
//        return "layout";
//    }
//
//
//    // ➕ Crear nueva evaluación
//    @GetMapping("/nuevo")
//    public String nuevo(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
//        String username = authentication.getName();
//
//        // Roles
//        boolean isAlumno = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"));
//        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
//        boolean isTutorCurso = authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"));
//
//        EvaluacionTutor evaluacion = new EvaluacionTutor();
//        evaluacion.setFecha(LocalDate.now());
//
//        if (isAlumno) {
//            Alumno alumno = alumnoService.obtenerPorEmail(username);
//            TutorPracticas tutorPracticas = alumno.getTutorPracticas();
//            Empresa empresa = alumno.getEmpresa();
//            Curso curso = alumno.getCurso();
//
//            if (yaEvaluadoRepository.existsByAlumnoIdAndTutorPracticasIdAndEmpresaIdAndCursoId(
//                    alumno.getId(), tutorPracticas.getId(), empresa.getId(), curso.getId())) {
//            	String mensaje = "Ya has realizado tu evaluación.";
//            	redirectAttributes.addFlashAttribute("error", mensaje);
//            	System.out.println("Se añadió flashattribute 'warning' antes del redirect.");
//                return "redirect:/alumno/dashboard";
//            }
//
//            TutorCurso tutorCurso = alumno.getCurso().getTutorCurso();
//     
//
//            evaluacion.setTutorCurso(tutorCurso);
//            evaluacion.setTutorPracticas(tutorPracticas);
//
//            // Selects solo con los tutores asociados al alumno
//            model.addAttribute("tutoresCurso", List.of(tutorCurso));
//            model.addAttribute("tutoresPracticas", List.of(tutorPracticas));
//
//            model.addAttribute("soloLectura", true); // deshabilita selects
//        }
//        else if (isTutorCurso) {
//            Optional<TutorCurso> optTutor = tutorCursoService.buscarPorEmail(username);
//
//            if (optTutor.isEmpty()) {
//            	String mensaje = "Ya has realizado tu evaluación.";
//            	redirectAttributes.addFlashAttribute("error", mensaje);
//                //redirectAttributes.addFlashAttribute("error", "No se encontró el tutor de curso asociado al usuario.");
//                System.out.println("Se añadió flashattribute 'warning' antes del redirect.");
//                return "redirect:/tutor/dashboard"; // no al login
//            }
//
//            TutorCurso tutor = optTutor.get();
//            evaluacion.setTutorCurso(tutor);
//
//            // Select de tutor curso solo él mismo
//            model.addAttribute("tutoresCurso", List.of(tutor));
//
//            // Obtener tutores de prácticas de los alumnos de sus cursos
//            List<TutorPracticas> tutoresPracticas = tutor.getCursos().stream()
//                    .flatMap(curso -> curso.getAlumnos().stream())
//                    .map(Alumno::getTutorPracticas)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//            model.addAttribute("tutoresPracticas", tutoresPracticas);
//
//            model.addAttribute("soloLectura", false); // puede seleccionar tutor de prácticas
//        }
//        else if (isAdmin) {
//            // Admin puede elegir cualquiera
//            List<TutorCurso> tutoresCurso = tutorCursoService.listarTodos();
//            List<TutorPracticas> tutoresPracticas = tutorPracticasService.listarTodos();
//
//            model.addAttribute("tutoresCurso", tutoresCurso);
//            model.addAttribute("tutoresPracticas", tutoresPracticas);
//
//            model.addAttribute("soloLectura", false); // editable
//        }
//        else {
//            return "null";
//        }
//
//        model.addAttribute("evaluacion", evaluacion);
//        model.addAttribute("viewName", "evaluaciontutor/form");
//
//        return "layout";
//    }
//
//
//
//
//    @GetMapping("/editar/{id}")
//    public String editar(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
//        String username = authentication.getName();
//        EvaluacionTutor evaluacion = evaluacionTutorService.buscarPorId(id)
//                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));
//
//        boolean soloLectura = true; // Por defecto, solo lectura
//
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
//            soloLectura = false; // El admin sí puede editar
//        }
//
//        model.addAttribute("evaluacion", evaluacion);
//        model.addAttribute("tutoresCurso", tutorCursoService.listarTodos());
//        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
//        model.addAttribute("soloLectura", soloLectura); // ✅ definir siempre
//        model.addAttribute("viewName", "evaluaciontutor/form");
//        return "layout";
//    }
//
//
//    // Endpoint AJAX para recargar tutores de prácticas
//    @GetMapping("/tutores-por-curso/{tutorCursoId}")
//    @ResponseBody
//    public List<com.control.practicas.dto.TutorPracticasDTO> obtenerTutoresPorCurso(@PathVariable Long tutorCursoId) {
//        return tutorPracticasService.listarTodos().stream()
//                .filter(tp -> tp.getAlumnos() != null && !tp.getAlumnos().isEmpty())
//                .filter(tp -> tp.getAlumnos().stream()
//                        .anyMatch(alumno -> alumno.getCurso() != null
//                                && alumno.getCurso().getTutorCurso() != null
//                                && alumno.getCurso().getTutorCurso().getId().equals(tutorCursoId)))
//                .distinct()
//                .map(tp -> new com.control.practicas.dto.TutorPracticasDTO(tp.getId(), tp.getNombre(), tp.getApellidos()))
//                .collect(Collectors.toList());
//    }
//    @PostMapping("/guardar")
//    public String guardar(@RequestParam(required = false) Long tutorCursoId,
//                          @RequestParam(required = false) Long tutorPracticasId,
//                          @RequestParam(required = false) BigDecimal puntuacion,
//                          @RequestParam(required = false) String observaciones,
//                          @RequestParam(required = false) String aspectosPositivos,
//                          @RequestParam(required = false) String aspectosMejorar,
//                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
//                          @RequestParam(required = false) Long id,
//                          Authentication authentication, 
//                          RedirectAttributes redirectAttributes) {
//        String username = authentication.getName();
//        
//        // 🔍 DEBUG: Ver qué está llegando
//        System.out.println("=== DEBUG EVALUACION ===");
//        System.out.println("ID: " + id);
//        System.out.println("TutorCurso ID: " + tutorCursoId);
//        System.out.println("TutorPracticas ID: " + tutorPracticasId);
//        System.out.println("Puntuación: " + puntuacion);
//        System.out.println("Fecha: " + fecha);
//        System.out.println("========================");
//        
//        try {
//            // Crear o buscar la evaluación
//            EvaluacionTutor evaluacion;
//            if (id != null) {
//                evaluacion = evaluacionTutorService.buscarPorId(id)
//                    .orElseThrow(() -> new RuntimeException("Evaluación no encontrada con ID: " + id));
//            } else {
//                evaluacion = new EvaluacionTutor();
//            }
//            
//            // 🔥 SOLUCIÓN ESPECIAL PARA ALUMNO
//            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
//                Alumno alumno = alumnoService.obtenerPorEmail(username);
//                
//                // Asignar directamente los tutores del alumno (ignora lo que venga del form)
//                evaluacion.setTutorCurso(alumno.getCurso().getTutorCurso());
//                evaluacion.setTutorPracticas(alumno.getTutorPracticas());
//                
//                System.out.println("🔹 ALUMNO: Tutores asignados desde el alumno autenticado");
//            } 
//            // Para ADMIN y TUTOR_CURSO, buscar por los IDs enviados
//            else {
//                // Si viene tutorCursoId, buscar la entidad completa
//                if (tutorCursoId != null) {
//                    TutorCurso tutorCurso = tutorCursoService.buscarPorId(tutorCursoId)
//                        .orElseThrow(() -> new RuntimeException("Tutor de curso no encontrado con ID: " + tutorCursoId));
//                    evaluacion.setTutorCurso(tutorCurso);
//                    System.out.println("**********////************* TutorCurso encontrado: " + tutorCurso.getNombre());
//                }
//                
//                // Si viene tutorPracticasId, buscar la entidad completa
//                if (tutorPracticasId != null) {
//                    TutorPracticas tutorPracticas = tutorPracticasService.buscarPorId(tutorPracticasId)
//                        .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado con ID: " + tutorPracticasId));
//                    evaluacion.setTutorPracticas(tutorPracticas);
//                    System.out.println("************///**************** TutorPracticas encontrado: " + tutorPracticas.getNombre());
//                }
//            }
//            
//            // Asignar los demás campos
//            evaluacion.setPuntuacion(puntuacion);
//            evaluacion.setObservaciones(observaciones);
//            evaluacion.setAspectosPositivos(aspectosPositivos);
//            evaluacion.setAspectosMejorar(aspectosMejorar);
//            
//            // Establecer fecha
//            if (fecha == null) {
//                evaluacion.setFecha(LocalDate.now());
//            } else {
//                evaluacion.setFecha(fecha);
//            }
//            
//            // Guardar
//            evaluacionTutorService.guardar(evaluacion);
//            System.out.println("✅ Evaluación guardada con ID: " + evaluacion.getId());
//            
//            if (id == null) {
//                redirectAttributes.addFlashAttribute("success", "Evaluación creada correctamente.");
//            } else {
//                redirectAttributes.addFlashAttribute("success", "Evaluación actualizada correctamente.");
//            }
//            
//        } catch (Exception e) {
//            System.err.println("❌ ERROR al guardar: " + e.getMessage());
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Error al guardar la evaluación: " + e.getMessage());
//        }
//        
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
//            Alumno alumno = alumnoService.obtenerPorEmail(authentication.getName());
//            
//            // Crear registro en YaEvaluado
//            YaEvaluado yaEvaluado = new YaEvaluado();
//            yaEvaluado.setAlumnoId(alumno.getId());
//            yaEvaluado.setCursoId(alumno.getCurso().getId());   
//            yaEvaluado.setEmpresaId(alumno.getEmpresa().getId());
//            yaEvaluado.setTutorPracticasId(alumno.getTutorPracticas().getId()); 
//            yaEvaluado.setFecha(LocalDateTime.now());
//            yaEvaluadoRepository.save(yaEvaluado);
//            
//            return "redirect:/alumno/dashboard";
//        }
//        return "redirect:/evaluaciontutor/listar";
//    }
//    // 🗑️ Eliminar evaluación (solo ADMIN)
//    @GetMapping("/eliminar/{id}")
//    public String eliminar(@PathVariable Long id, 
//                           Authentication authentication, 
//                           RedirectAttributes redirectAttributes) {
//        
//        // Solo ADMIN puede eliminar
//        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
//            redirectAttributes.addFlashAttribute("error", "No tienes permisos para eliminar evaluaciones.");
//            return "redirect:/evaluaciontutor/listar";
//        }
//        
//        try {
//            evaluacionTutorService.eliminar(id);
//            redirectAttributes.addFlashAttribute("success", "Evaluación eliminada correctamente.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Error al eliminar la evaluación: " + e.getMessage());
//        }
//        
//        return "redirect:/evaluaciontutor/listar";
//    }
//    
//    
//    
//    
//}

package com.control.practicas.controllers;

import com.control.practicas.dto.EvaluacionTutorDTO;
import com.control.practicas.models.*;
import com.control.practicas.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/evaluaciontutor")
public class EvaluacionTutorController {

    private final EvaluacionTutorService evaluacionTutorService;
    private final TutorCursoService tutorCursoService;
    private final TutorPracticasService tutorPracticasService;
    private final AlumnoService alumnoService; // 👈 Necesario para el rol ALUMNO

    public EvaluacionTutorController(EvaluacionTutorService evaluacionTutorService,
                                     TutorCursoService tutorCursoService,
                                     TutorPracticasService tutorPracticasService,
                                     AlumnoService alumnoService) {
        this.evaluacionTutorService = evaluacionTutorService;
        this.tutorCursoService = tutorCursoService;
        this.tutorPracticasService = tutorPracticasService;
        this.alumnoService = alumnoService;
    }

    // 📋 Listar evaluaciones según el rol
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

        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
        	Alumno alumno = alumnoService.obtenerPorEmail(username);
            evaluaciones = evaluacionTutorService.listarPorTutorPracticas(alumno.getTutorPracticas().getId());

        } else {
            evaluaciones = List.of();
        }

        model.addAttribute("evaluaciones", evaluaciones);
        model.addAttribute("viewName", "evaluaciontutor/listar");
        return "layout";
    }

    // ➕ Crear nueva evaluación
    @GetMapping("/nuevo")
    public String nuevo(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();

        // Si es un alumno el que accede
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
            Alumno alumno = alumnoService.obtenerPorEmail(username);
            TutorPracticas tutorPracticas = alumno.getTutorPracticas();

            // ✅ Comprobar si ya hay evaluación para ese tutor de prácticas
            Optional<EvaluacionTutor> evaluacionExistenteOpt =
                    evaluacionTutorService.buscarPorTutorPracticasId(tutorPracticas.getId());

            if (evaluacionExistenteOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("warning", "Ya has realizado una evaluación. Solo puedes editarla.");

                EvaluacionTutor evaluacionExistente = evaluacionExistenteOpt.get();
                return "redirect:/evaluaciontutor/editar/" + evaluacionExistente.getId();
            }

            // ✅ Si no hay evaluación, crear una nueva
            EvaluacionTutor evaluacion = new EvaluacionTutor();
            evaluacion.setFecha(LocalDate.now());
            evaluacion.setTutorCurso(alumno.getCurso().getTutorCurso());
            evaluacion.setTutorPracticas(tutorPracticas);

            model.addAttribute("evaluacion", evaluacion);
            model.addAttribute("soloLectura", true);
            model.addAttribute("viewName", "evaluaciontutor/form");
            return "layout";
        }

        // Si no es alumno (por ejemplo, un administrador o tutor)
        EvaluacionTutor evaluacion = new EvaluacionTutor();
        evaluacion.setFecha(LocalDate.now());
        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("tutoresCurso", tutorCursoService.listarTodos());
        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    // ✏️ Editar (solo suya si es alumno)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        EvaluacionTutor evaluacion = evaluacionTutorService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Evaluación no encontrada"));

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
        	Alumno alumno = alumnoService.obtenerPorEmail(username);
            // 🔒 Solo puede editar la suya
            if (!evaluacion.getTutorPracticas().getId().equals(alumno.getTutorPracticas().getId())) {
                redirectAttributes.addFlashAttribute("error", "No puedes editar esta evaluación.");
                return "redirect:/evaluaciontutor/listar";
            }
        }

        model.addAttribute("evaluacion", evaluacion);
        model.addAttribute("tutoresCurso", tutorCursoService.listarTodos());
        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
        model.addAttribute("viewName", "evaluaciontutor/form");
        return "layout";
    }

    // 🔹 Endpoint AJAX para recargar tutores de prácticas
    @GetMapping("/tutores-por-curso/{tutorCursoId}")
    @ResponseBody
    public List<com.control.practicas.dto.TutorPracticasDTO> obtenerTutoresPorCurso(@PathVariable Long tutorCursoId) {
        return tutorPracticasService.listarTodos().stream()
                .filter(tp -> tp.getAlumnos() != null && !tp.getAlumnos().isEmpty())
                .filter(tp -> tp.getAlumnos().stream()
                        .anyMatch(alumno -> alumno.getCurso() != null
                                && alumno.getCurso().getTutorCurso() != null
                                && alumno.getCurso().getTutorCurso().getId().equals(tutorCursoId)))
                .distinct()
                .map(tp -> new com.control.practicas.dto.TutorPracticasDTO(tp.getId(), tp.getNombre(), tp.getApellidos()))
                .collect(Collectors.toList());
    }
}

package com.control.practicas.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.dto.AlumnoDTO;
import com.control.practicas.models.Alumno;
import com.control.practicas.models.Incidencia;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.TutorCursoRepository;
import com.control.practicas.repositories.TutorPracticasRepository;
import com.control.practicas.services.IncidenciaService;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.TutorPracticasService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/incidencia")
public class IncidenciaController {

    private final CommonController commonController;

    private final IncidenciaService incidenciaService;
    private final AlumnoService alumnoService;
    private final TutorPracticasService tutorPracticasService;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final TutorCursoRepository tutorCursoRepository;
    private final AlumnoRepository alumnoRepository;
    // Inyección de dependencias por constructor
    public IncidenciaController(IncidenciaService incidenciaService,
                               AlumnoService alumnoService,
                               TutorCursoRepository tutorCursoRepository,
                               AlumnoRepository alumnoRepository,
                               TutorPracticasRepository tutorPracticasRepository,
                               TutorPracticasService tutorPracticasService, CommonController commonController) {
        this.incidenciaService = incidenciaService;
        this.alumnoService = alumnoService;
        this.tutorPracticasService = tutorPracticasService;
        this.commonController = commonController;
        this.alumnoRepository=alumnoRepository;
        this.tutorPracticasRepository= tutorPracticasRepository;
        this.tutorCursoRepository=tutorCursoRepository;
    }
    
    @GetMapping({"/listar", ""})
    public String listar(Model model,
                        @RequestParam(required = false) String estado,
                        @RequestParam(required = false) String tipo,
                        @RequestParam(required = false) String alumno,
                        Authentication authentication) {

        List<Incidencia> incidencias = incidenciaService.listarTodas();

        // Filtrar por rol
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_PRACTICAS"))) {
            String email = authentication.getName();
            TutorPracticas tutorP = tutorPracticasRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado"));
            List<Alumno> alumnosTutor = alumnoRepository.findByTutorPracticasId(tutorP.getId());
            incidencias = incidencias.stream()
                .filter(i -> alumnosTutor.contains(i.getAlumno()))
                .collect(Collectors.toList());
        } 
        else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            String email = authentication.getName();
            TutorCurso tutorC = tutorCursoRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tutor de curso no encontrado"));
            List<Alumno> alumnosTutor = alumnoRepository.findByTutorCursoId(tutorC.getId());
            incidencias = incidencias.stream()
                .filter(i -> alumnosTutor.contains(i.getAlumno()))
                .collect(Collectors.toList());
            }
        // Si es ADMIN o cualquier otro rol, muestra todas (no filtra)

        // Aplicar filtros
        if (estado != null && !estado.isEmpty()) {
            incidencias = incidencias.stream()
                .filter(i -> i.getEstado() != null && i.getEstado().name().equals(estado))
                .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isEmpty()) {
            incidencias = incidencias.stream()
                .filter(i -> i.getTipo() != null && i.getTipo().name().equals(tipo))
                .collect(Collectors.toList());
        }

        if (alumno != null && !alumno.isEmpty()) {
            String alumnoLower = alumno.toLowerCase();
            incidencias = incidencias.stream()
                .filter(i -> i.getAlumno() != null &&
                    (i.getAlumno().getNombre().toLowerCase().contains(alumnoLower) ||
                     i.getAlumno().getApellidos().toLowerCase().contains(alumnoLower)))
                .collect(Collectors.toList());
        }

        model.addAttribute("incidencias", incidencias);
        model.addAttribute("viewName", "incidencia/listar");

        // Mantener los valores del filtro en el formulario
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroAlumno", alumno);

        return "layout";
    }

 // Listar todas las incidencias
  /*  @GetMapping({"/listar", ""})
    public String listar(Model model,
                        @RequestParam(required = false) String estado,
                        @RequestParam(required = false) String tipo,
                        @RequestParam(required = false) String alumno) {

        List<Incidencia> incidencias = incidenciaService.listarTodas();
        
        // Aplicar filtros
        if (estado != null && !estado.isEmpty()) {
            incidencias = incidencias.stream()
                .filter(i -> i.getEstado() != null && i.getEstado().name().equals(estado))
                .collect(Collectors.toList());
        }
        
        if (tipo != null && !tipo.isEmpty()) {
            incidencias = incidencias.stream()
                .filter(i -> i.getTipo() != null && i.getTipo().name().equals(tipo))
                .collect(Collectors.toList());
        }
        
        if (alumno != null && !alumno.isEmpty()) {
            String alumnoLower = alumno.toLowerCase();
            incidencias = incidencias.stream()
                .filter(i -> i.getAlumno() != null && 
                    (i.getAlumno().getNombre().toLowerCase().contains(alumnoLower) ||
                     i.getAlumno().getApellidos().toLowerCase().contains(alumnoLower)))
                .collect(Collectors.toList());
        }
        
        model.addAttribute("incidencias", incidencias);
        model.addAttribute("viewName", "incidencia/listar");
        
        // Mantener los valores del filtro en el formulario
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroAlumno", alumno);
        
        return "layout";
    */
/*
    // Mostrar formulario para nueva incidencia
    @GetMapping("/nueva")
    public String nuevaIncidencia(Model model) {
        model.addAttribute("incidencia", new Incidencia());
        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
        model.addAttribute("alumnos", alumnoService.listarTodos());
        model.addAttribute("viewName", "incidencia/form");
        return "layout";
    }
*/
 /*   @GetMapping("/alumnos-por-tutor/{tutorId}")
    @ResponseBody
    public List<Map<String, Object>> obtenerAlumnosPorTutor(@PathVariable Long tutorId) {
        List<Alumno> alumnos = alumnoService.listarPorTutorPracticas(tutorId);
        
        // Convertir a JSON simple
        return alumnos.stream()
            .map(a -> Map.of(
                "id", a.getId(),
                "nombre", a.getNombre() + " " + a.getApellidos()
            ))
            .collect(Collectors.toList());
    }*/
    
    @GetMapping("/alumnos-por-tutor/{tutorId}")
    @ResponseBody
    public List<AlumnoDTO> obtenerAlumnosPorTutor(@PathVariable Long tutorId) {
        List<Alumno> alumnos = alumnoService.listarPorTutorPracticas(tutorId);
        
        return alumnos.stream()
            .map(a -> new AlumnoDTO(a.getId(), a.getNombre() + " " + a.getApellidos()))
            .collect(Collectors.toList());
    } 
    
    @GetMapping({"/nueva", "/editar/{id}"})
    public String formIncidencia(@PathVariable(required = false) Long id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (id != null) {
            // 🟡 Modo edición
            return incidenciaService.buscarPorId(id)
                    .map(incidencia -> {
                        model.addAttribute("incidencia", incidencia);

                        var tutor = incidencia.getTutorPracticas();
                        List<Alumno> alumnos = (tutor != null)
                                ? alumnoService.listarPorTutorPracticas(tutor.getId())
                                : Collections.emptyList();

                        model.addAttribute("alumnos", alumnos);
                        model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
                        model.addAttribute("viewName", "incidencia/form");
                        return "layout";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("error", "Incidencia no encontrada");
                        return "redirect:/incidencia/listar";
                    });

        } else {
            // 🟢 Modo nuevo
            model.addAttribute("incidencia", new Incidencia());
            model.addAttribute("alumnos", Collections.emptyList()); // inicialmente vacío
            model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
            model.addAttribute("viewName", "incidencia/form");
            return "layout";
        }
    }

    
    
    
    
    // Mostrar formulario para editar incidencia
   /* @GetMapping("/editar/{id}")
    public String editarIncidencia(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return incidenciaService.buscarPorId(id)
                .map(incidencia -> {
                    model.addAttribute("incidencia", incidencia);
                    model.addAttribute("alumnos", alumnoService.listarTodos());
                    model.addAttribute("tutoresPracticas", tutorPracticasService.listarTodos());
                    model.addAttribute("viewName", "incidencia/form");
                    return "layout";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Incidencia no encontrada");
                    return "redirect:/incidencia/listar";
                });
    }*/
    // Guardar incidencia (crear o actualizar)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Incidencia incidencia, RedirectAttributes redirectAttributes) {
        try {
            // Si el estado es RESUELTA y no tiene fecha de resolución, la establecemos
            if (incidencia.getEstado() == Incidencia.Estado.RESUELTA && 
                incidencia.getFechaResolucion() == null) {
                incidencia.setFechaResolucion(LocalDateTime.now());
            }
            
            incidenciaService.guardar(incidencia);
            
            String mensaje = incidencia.getId() != null ? 
                "Incidencia actualizada correctamente" : 
                "Incidencia creada correctamente";
            
            redirectAttributes.addFlashAttribute("success", mensaje);
            
            return "redirect:/incidencia/listar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la incidencia: " + e.getMessage());
            return "redirect:/incidencia/nueva";
        }
        
    }

    // Eliminar incidencia
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            if (incidenciaService.existePorId(id)) {
                incidenciaService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Incidencia eliminada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Incidencia no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la incidencia: " + e.getMessage());
        }
        return "redirect:/incidencia/listar";
    }

    // Cambiar estado de una incidencia (opcional)
    @PostMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, 
                               @RequestParam Incidencia.Estado nuevoEstado,
                               RedirectAttributes redirectAttributes) {
        return incidenciaService.buscarPorId(id)
                .map(incidencia -> {
                    incidencia.setEstado(nuevoEstado);
                    
                    // Si se marca como resuelta, establecer fecha de resolución
                    if (nuevoEstado == Incidencia.Estado.RESUELTA && 
                        incidencia.getFechaResolucion() == null) {
                        incidencia.setFechaResolucion(LocalDateTime.now());
                    }
                    
                    incidenciaService.guardar(incidencia);
                    redirectAttributes.addFlashAttribute("success", "Estado actualizado correctamente");
                    return "redirect:/incidencia/listar";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Incidencia no encontrada");
                    return "redirect:/incidencia/listar";
                });
    }
}
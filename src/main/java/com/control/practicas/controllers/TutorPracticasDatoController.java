package com.control.practicas.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.ObservacionDiaria;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.models.Usuario;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.CursoRepository;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import com.control.practicas.repositories.TutorPracticasRepository;
import com.control.practicas.repositories.UsuarioRepository;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.CursoService;
import com.control.practicas.services.EmpresaService;
import com.control.practicas.services.ObservacionDiariaService;
import com.control.practicas.services.TutorPracticasService;

@Controller
@RequestMapping("/tutorpracticas")
public class TutorPracticasDatoController {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final UsuarioRepository usuarioRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasService tutorPracticasService;
    
    
    public TutorPracticasDatoController(
            ObservacionDiariaRepository observacionDiariaRepository,
            ObservacionDiariaService observacionDiariaService,
            AlumnoRepository alumnoRepository,
            AlumnoService alumnoService,
            CursoRepository cursoRepository,
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            TutorPracticasRepository tutorPracticasRepository,
            TutorPracticasService tutorPracticasService) {
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.alumnoRepository = alumnoRepository;
        this.alumnoService = alumnoService;
        this.usuarioRepository = usuarioRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        this.cursoRepository=cursoRepository;
        this.empresaRepository=empresaRepository;
        this.tutorPracticasService=tutorPracticasService;
    }
    
    // Método auxiliar para obtener el tutor de prácticas autenticado
    private TutorPracticas getTutorAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        return tutorPracticasRepository.findById(user.getReferenceId())
            .orElseThrow(() -> new RuntimeException("Tutor de prácticas no encontrado"));
    }
   
    
    
    @GetMapping("/observaciondiaria/listar")
    public String listar(
            @RequestParam(required = false) Long alumnoId,
            Model model, 
            Authentication authentication) {
        
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);
            
            List<Alumno> alumnosDelTutor = alumnoRepository.findByTutorPracticas(tutor);
            
            List<ObservacionDiaria> observaciones;
            Alumno alumnoSeleccionado = null;
            
            if (alumnoId != null) {
                alumnoSeleccionado = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
                
                if (!alumnoSeleccionado.getTutorPracticas().getId().equals(tutor.getId())) {
                    throw new RuntimeException("No tiene permisos para ver las observaciones de este alumno");
                }
                
                observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumnoId);
            } else {
                if (alumnosDelTutor.isEmpty()) {
                    observaciones = List.of();
                } else {
                    observaciones = observacionDiariaRepository.findByAlumnoInOrderByFechaDesc(alumnosDelTutor);
                }
            }
            
         //   model.addAttribute("observaciones", observaciones);
            model.addAttribute("ObservacionDiaria", observaciones); 
            model.addAttribute("alumnosDelTutor", alumnosDelTutor);
            model.addAttribute("alumnoSeleccionado", alumnoSeleccionado);
            model.addAttribute("tutorActual", tutor);
            model.addAttribute("tieneObservaciones", !observaciones.isEmpty());
            model.addAttribute("viewName", "admin/alumno/observaciondiaria/observaciones");
            return "layout";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("viewName", "error");
            return "layout";
        }
    }

    @GetMapping("/observaciondiaria/editar/{id}")
    public String ver(@PathVariable Long id, Model model, Authentication authentication) {
        TutorPracticas tutor = getTutorAutenticado(authentication);
        ObservacionDiaria observacionDiaria = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
        
        if (!observacionDiaria.getAlumno().getTutorPracticas().getId().equals(tutor.getId())) {
            throw new RuntimeException("No tiene permisos para ver esta observación");
        }
        
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", observacionDiaria.getAlumno());
        model.addAttribute("tutorActual", tutor);
        model.addAttribute("soloLectura", false);
        model.addAttribute("viewName", "admin/alumno/observaciondiaria/form"); 
        return "layout";
    }

    @PostMapping("/observaciondiaria/guardar")
    public String guardar(
            @ModelAttribute ObservacionDiaria observacionDiaria,
            @RequestParam(required = false) String observacionesTutor,
            Authentication authentication, 
            RedirectAttributes redirectAttributes) {
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);
            
            ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacionDiaria.getId())
                .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
            
            if (!observacionExistente.getAlumno().getTutorPracticas().getId().equals(tutor.getId())) {
                throw new RuntimeException("No tiene permisos para editar esta observación");
            }
            
            observacionExistente.setObservacionesTutor(observacionesTutor);
            observacionDiariaService.guardar(observacionExistente);
            redirectAttributes.addFlashAttribute("success", "Observaciones del tutor actualizadas exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar las observaciones: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/tutorpracticas/observaciondiaria/listar"; 
    }

    
    @GetMapping("/alumno")
    public String listarAlumnosDelTutor(Model model, 
    		Authentication authentication,
    		RedirectAttributes redirectAttributes) {
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);

            List<Alumno> alumnosDelTutor = alumnoRepository.findByTutorPracticas(tutor);
            model.addAttribute("alumnos", alumnosDelTutor);
            model.addAttribute("mostrarBotonNuevo", false);

            model.addAttribute("alumnosDelTutor", alumnosDelTutor);
            model.addAttribute("tutorActual", tutor);
            model.addAttribute("viewName", "admin/alumno/listar");
            return "layout";

        } catch (Exception e) {
        	 redirectAttributes.addFlashAttribute("error", "Error al guardar las observaciones: " + e.getMessage());
             e.printStackTrace();
             model.addAttribute("viewName", "admin/alumno/listar");
             return "layout";
        }
    }
    @GetMapping("/alumno/editar/{id}")
    public String editarAlumnoComoTutor(@PathVariable Long id,
                                        Model model,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);

            Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            if (!alumno.getTutorPracticas().getId().equals(tutor.getId())) {
                throw new RuntimeException("No tiene permisos para editar este alumno");
            }
            
            List<Curso> cursos = alumno.getCurso() != null 
                ? List.of(alumno.getCurso()) 
                : List.of();
                
            List<Empresa> empresas = alumno.getEmpresa() != null 
                ? List.of(alumno.getEmpresa()) 
                : List.of();
                
            List<TutorPracticas> tutores = List.of(tutor);
            
            model.addAttribute("alumno", alumno); // ⚠️ CRÍTICO - debe ser "alumno" exactamente
            model.addAttribute("cursos", cursos);
            model.addAttribute("empresas", empresas);
            model.addAttribute("tutores", tutores);
            model.addAttribute("horario", alumno.getHorario());
            model.addAttribute("modo", "editar");
            model.addAttribute("esTutorPracticas", true);
            model.addAttribute("viewName", "admin/alumno/form");
            return "layout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el alumno: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/tutorpracticas/alumno/listar";
        }
    }    
    @PostMapping("/alumno/guardar")
    public String guardar(@ModelAttribute Alumno alumno,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        try {
            TutorPracticas tutor = getTutorAutenticado(authentication);
            
            // DEBUG: Verificar qué llega
            System.out.println("Alumno ID: " + alumno.getId());
            System.out.println("Curso ID: " + alumno.getCursoId());
            System.out.println("Empresa ID: " + alumno.getEmpresaId());
            System.out.println("Tutor ID: " + alumno.getTutorPracticasId());
            
            // Validar que el alumno pertenece al tutor (si está editando)
            if (alumno.getId() != null) {
                Alumno alumnoExistente = alumnoRepository.findById(alumno.getId())
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
                    
                if (!alumnoExistente.getTutorPracticas().getId().equals(tutor.getId())) {
                    throw new RuntimeException("No tiene permisos para editar este alumno");
                }
                
                // Si cursoId es null, mantener el curso existente
                if (alumno.getCursoId() == null && alumnoExistente.getCurso() != null) {
                    alumno.setCurso(alumnoExistente.getCurso());
                }
                
                // Si empresaId es null, mantener la empresa existente
                if (alumno.getEmpresaId() == null && alumnoExistente.getEmpresa() != null) {
                    alumno.setEmpresa(alumnoExistente.getEmpresa());
                }
            }
            
            // Cargar el curso desde la BD si viene el ID
            if (alumno.getCursoId() != null && alumno.getCursoId() > 0) {
                Curso curso = cursoRepository.findById(alumno.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
                alumno.setCurso(curso);
            }
            
            // Validar que al final tenga curso
            if (alumno.getCurso() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un curso");
                return "redirect:/tutorpracticas/alumno/editar/" + alumno.getId();
            }
            
            // Cargar la empresa desde la BD si existe
            if (alumno.getEmpresaId() != null && alumno.getEmpresaId() > 0) {
                Empresa empresa = empresaRepository.findById(alumno.getEmpresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                alumno.setEmpresa(empresa);
            }
            
            // Mantener el tutor de prácticas (él mismo)
            alumno.setTutorPracticas(tutor);
            
            alumnoService.guardar(alumno);
            redirectAttributes.addFlashAttribute("success", "Alumno actualizado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el alumno: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/tutorpracticas/alumno";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Obtenemos el tutor autenticado
        TutorPracticas tutor = getTutorAutenticado(authentication);

        // Empresa asociada al tutor
        Empresa empresa = tutor.getEmpresa();

        // Lista de alumnos del tutor
        List<Alumno> alumnos = alumnoRepository.findByTutorPracticas(tutor);

        // Contar observaciones e incidencias
        long totalObservaciones = tutorPracticasService.contarObservaciones(tutor);
        long totalIncidencias = tutorPracticasService.contarIncidencias(tutor);

        // Obtener horas por alumno desde el servicio
        Map<String, Integer> horasPorAlumno = tutorPracticasService.obtenerHorasPorAlumno(alumnos);
        Map<Long, Integer> horasPendientes = tutorPracticasService.obtenerHorasPendientesPorAlumno(alumnos);
        
        model.addAttribute("horasPendientes", horasPendientes);
        model.addAttribute("tutor", tutor);
        model.addAttribute("empresa", empresa);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("horasPorAlumno", horasPorAlumno);
        model.addAttribute("totalObservaciones", totalObservaciones);
        model.addAttribute("totalIncidencias", totalIncidencias);

        model.addAttribute("pageTitle", "Dashboard Tutor Practicas");
        model.addAttribute("viewName", "tutorpracticas/dashboard");

        return "layout";
    }

    
    
}
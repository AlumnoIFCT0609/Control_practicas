package com.control.practicas.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.ObservacionDiaria;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.models.Usuario;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import com.control.practicas.repositories.UsuarioRepository;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.ObservacionDiariaService;

@Controller
@RequestMapping("/alumno")
public class AlumnoDatoController {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final UsuarioRepository usuarioRepository;
    
    public AlumnoDatoController(ObservacionDiariaRepository observacionDiariaRepository,
                                      ObservacionDiariaService observacionDiariaService,
                                      AlumnoRepository alumnoRepository,
                                      AlumnoService alumnoService,
                                      UsuarioRepository usuarioRepository) {
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alumnoService= alumnoService;
    }
    
    
    
    
    
    // Método auxiliar para obtener el alumno autenticado
    private Alumno getAlumnoAutenticado(Authentication authentication) {
        String email = authentication.getName();
        Usuario user = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // El referenceId del usuario apunta al ID del alumno
        return alumnoRepository.findById(user.getReferenceId())
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
    }

    @GetMapping("/perfil")
    public String verPerfilAlumno(Authentication authentication, 
    		Model model) {

        // Obtener el email del usuario autenticado
        String email = authentication.getName();

        // Buscar el alumno correspondiente
        
        Alumno alumno = alumnoService.findByEmailUsuario(email)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado con email: " + email));

        // Adaptar datos para el formulario reutilizado
        if (alumno.getCurso() != null) {
            alumno.setCursoId(alumno.getCurso().getId());
        }
        if (alumno.getEmpresa() != null) {
            alumno.setEmpresaId(alumno.getEmpresa().getId());
        }
        if (alumno.getTutorPracticas() != null) {
            alumno.setTutorPracticasId(alumno.getTutorPracticas().getId());
        }

        // ⚠️ El formulario espera listas, así que le pasamos listas con un solo elemento
        List<Curso> cursos = alumno.getCurso() != null ? List.of(alumno.getCurso()) : List.of();
        List<Empresa> empresas = alumno.getEmpresa() != null ? List.of(alumno.getEmpresa()) : List.of();
        List<TutorPracticas> tutores = alumno.getTutorPracticas() != null ? List.of(alumno.getTutorPracticas()) : List.of();

        // Añadir atributos requeridos por el form
        model.addAttribute("alumno", alumno);
        model.addAttribute("cursos", cursos);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutores", tutores);

        // Flags para la vista
        model.addAttribute("esTutorPracticas", false);
        model.addAttribute("esVistaAlumno", true);

        // Reutiliza la vista del admin
        model.addAttribute("viewName", "admin/alumno/form");

        return "layout";
    }
    
    @PostMapping("/perfil")
    public String guardarPerfilAlumno(@ModelAttribute("alumno") Alumno alumno, 
                                      Authentication authentication, 
                                      RedirectAttributes redirectAttrs) {
        String email = authentication.getName();
        Alumno alumnoActual = alumnoService.findByEmailUsuario(email)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // Solo permitir que modifique algunos campos
        alumnoActual.setTelefono(alumno.getTelefono());
        alumnoActual.setHorario(alumno.getHorario());
        alumnoActual.setDuracionPracticas(alumno.getDuracionPracticas());
        alumnoActual.setFechaInicio(alumno.getFechaInicio());
        alumnoActual.setFechaFin(alumno.getFechaFin());
        // ⚠️ No modificar curso, empresa, tutor, DNI, etc.

        alumnoService.guardar(alumnoActual);

        redirectAttrs.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");

        return "redirect:/alumno/dashboard";
    }

    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication,
    						Model model) {

        List<Alumno> alumnos = alumnoService.listarTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("pageTitle", "Dashboard Alumno");
        model.addAttribute("viewName", "alumno/dashboard"); // Para que tu layout lo incluya
        return "layout";  // O el nombre del template principal que uses
    }
    
    @GetMapping("/observaciondiaria/listar")
    public String listar(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        List<ObservacionDiaria> observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumno.getId());
        
        model.addAttribute("observaciones", observaciones);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("tieneObservaciones", !observaciones.isEmpty());
        model.addAttribute("viewName", "alumno/observaciondiaria/listar");
        return "layout";
    }
    
    @GetMapping("/observaciondiaria/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        ObservacionDiaria observacionDiaria = new ObservacionDiaria();
       // model.addAttribute("observacionDiaria", new ObservacionDiaria());
        observacionDiaria.setHorasRealizadas(0); // ← INICIALIZA ESTO
        observacionDiaria.setFecha(LocalDate.now());
        
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    @GetMapping("/observaciondiaria/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication) {
        Alumno alumno = getAlumnoAutenticado(authentication);
        ObservacionDiaria observacionDiaria = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
        
        // Verificar que la observación pertenece al alumno autenticado
        if (!observacionDiaria.getAlumno().getId().equals(alumno.getId())) {
            throw new RuntimeException("No tiene permisos para editar esta observación");
        }
        
        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", alumno);
        model.addAttribute("viewName", "alumno/observaciondiaria/form");
        return "layout";
    }
    
    /* Error al guardar la observación: could not execute statement [Column 'fecha' cannot be null] 
     * [update observaciondiaria set actividades=?,alumno=?,explicaciones=?,fecha=?,
     * horas_realizadas=?,observaciones_alumno=?,observaciones_tutor=? where id=?]; 
     * SQL [update observaciondiaria set actividades=?,alumno=?,explicaciones=?,
     * fecha=?,horas_realizadas=?,observaciones_alumno=?,observaciones_tutor=? where id=?]; 
     * constraint [null]*/
    
    @PostMapping("/observaciondiaria/guardar")
    public String guardar(@ModelAttribute ObservacionDiaria observacionDiaria, 
                         Authentication authentication, 
                         RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            
            /* Si es edición, verificar que pertenece al alumno*/
            if (observacionDiaria.getId() != null) {
                ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacionDiaria.getId())
                    .orElseThrow(() -> new RuntimeException("Observación no encontrada"));
                
                if (!observacionExistente.getAlumno().getId().equals(alumno.getId())) {
                    throw new RuntimeException("No tiene permisos para editar esta observación");
                }
                
                // Preservar las observaciones del tutor
                observacionDiaria.setObservacionesTutor(observacionExistente.getObservacionesTutor());
            }
            
            // Asignar el alumno autenticado
            observacionDiaria.setAlumno(alumno);
            
            observacionDiariaService.guardar(observacionDiaria);
            redirectAttributes.addFlashAttribute("success", 
                    observacionDiaria.getId() == null ? "Observación creada exitosamente" : "Observación actualizada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la observación: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/alumno/observaciondiaria/listar";
    }
    
    @GetMapping("/observaciondiaria/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Alumno alumno = getAlumnoAutenticado(authentication);
            Optional<ObservacionDiaria> observacionOpt = observacionDiariaService.buscarPorId(id);
            
            if (observacionOpt.isPresent()) {
                ObservacionDiaria observacionDiaria = observacionOpt.get();
                
                // Verificar que pertenece al alumno
                if (!observacionDiaria.getAlumno().getId().equals(alumno.getId())) {
                    throw new RuntimeException("No tiene permisos para eliminar esta observación");
                }
                
                observacionDiariaService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Observación eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Observación no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la observación: " + e.getMessage());
        }
        return "redirect:/alumno/observaciondiaria/listar";
    }
}
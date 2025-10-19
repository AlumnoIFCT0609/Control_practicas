package controllers;

import models.*;
import repositories.*;
import services.AlumnoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/alumno")
public class AlumnoController {
    
    private final UserRepository userRepository;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    
    public AlumnoController(UserRepository userRepository,
                          AlumnoRepository alumnoRepository,
                          AlumnoService alumnoService,
                          CursoRepository cursoRepository,
                          EmpresaRepository empresaRepository,
                          TutorPracticasRepository tutorPracticasRepository) {
        this.userRepository = userRepository;
        this.alumnoRepository = alumnoRepository;
        this.alumnoService = alumnoService;
        this.cursoRepository = cursoRepository;
        this.empresaRepository = empresaRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Optional<Usuario> userOpt = userRepository.findByEmail(auth.getName());
        
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            
            if (usuario.getReferenceId() != null) {
                Optional<Alumno> alumnoOpt = alumnoRepository.findById(usuario.getReferenceId());
                if (alumnoOpt.isPresent()) {
                    Alumno alumno = alumnoOpt.get();
                    model.addAttribute("alumno", alumno);
                    model.addAttribute("curso", alumno.getCurso());
                    model.addAttribute("empresa", alumno.getEmpresa());
                    model.addAttribute("fechaInicio", alumno.getFechaInicio());
                    model.addAttribute("fechaFin", alumno.getFechaFin());
                    model.addAttribute("duracionPracticas", alumno.getDuracionPracticas());
                    model.addAttribute("horario", alumno.getHorario());
                    model.addAttribute("tutorPracticas", alumno.getTutorPracticas());
                    model.addAttribute("viewName", "alumno/dashboard");
                }
            }
        }
        return "layout";
    }
    
    // Listar todos los alumnos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Alumno> alumnos = alumnoService.listarTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("viewName", "admin/alumno/listar");
        return "layout";
    }
    
    // Mostrar formulario para crear nuevo alumno
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("alumno", new Alumno());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("tutores", tutorPracticasRepository.findAll());
        model.addAttribute("viewName", "admin/alumno/form");
        return "layout";
    }
    
    // Mostrar formulario para editar alumno existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Alumno> alumnoOpt = alumnoService.buscarPorId(id);
        
        if (alumnoOpt.isPresent()) {
            model.addAttribute("alumno", alumnoOpt.get());
            model.addAttribute("cursos", cursoRepository.findAll());
            model.addAttribute("empresas", empresaRepository.findAll());
            model.addAttribute("tutores", tutorPracticasRepository.findAll());
            model.addAttribute("viewName", "admin/alumno/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Alumno no encontrado");
            return "redirect:/admin/alumno/listar";
        }
    }
    
    // Guardar o actualizar alumno
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alumno alumno, RedirectAttributes redirectAttributes) {
        try {
            alumnoService.guardar(alumno);
            redirectAttributes.addFlashAttribute("success", 
                alumno.getId() == null ? "Alumno creado exitosamente" : "Alumno actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el alumno: " + e.getMessage());
        }
        return "redirect:/admin/alumno/listar";
    }
    
        
    // Eliminar alumno
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Alumno> alumnoOpt = alumnoService.buscarPorId(id);
            if (alumnoOpt.isPresent()) {
                alumnoService.eliminar(id);
                redirectAttributes.addFlashAttribute("success", "Alumno eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Alumno no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el alumno: " + e.getMessage());
        }
        return "redirect:/admin/alumno/listar";
    }
}

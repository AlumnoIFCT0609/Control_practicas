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
   
    
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Alumno> alumnos = alumnoService.listarTodos();
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("viewName", "admin/alumno/listar");
        return "layout";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("alumno", new Alumno());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("tutores", tutorPracticasRepository.findAll());
        model.addAttribute("viewName", "admin/alumno/form");
        return "layout";
    }
    
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
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alumno alumno, 
                         @RequestParam(name = "curso.id", required = false) Long cursoId,
                         @RequestParam(name = "empresa.id", required = false) Long empresaId,
                         @RequestParam(name = "tutorPracticas.id", required = false) Long tutorPracticasId,
                         RedirectAttributes redirectAttributes) {
        try {
            // Validar curso obligatorio
            if (cursoId == null || cursoId == 0) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un curso");
                return "redirect:/admin/alumno/nuevo";
            }
            
            // Crear y asignar el curso
            Curso curso = new Curso();
            curso.setId(cursoId);
            alumno.setCurso(curso);
            
            // Crear y asignar la empresa si existe
            if (empresaId != null && empresaId > 0) {
                Empresa empresa = new Empresa();
                empresa.setId(empresaId);
                alumno.setEmpresa(empresa);
            } else {
                alumno.setEmpresa(null);
            }
            
            // Crear y asignar el tutor si existe
            if (tutorPracticasId != null && tutorPracticasId > 0) {
                TutorPracticas tutor = new TutorPracticas();
                tutor.setId(tutorPracticasId);
                alumno.setTutorPracticas(tutor);
            } else {
                alumno.setTutorPracticas(null);
            }
            
            alumnoService.guardar(alumno);
            redirectAttributes.addFlashAttribute("success", 
                alumno.getId() == null ? "Alumno creado exitosamente" : "Alumno actualizado exitosamente");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el alumno: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/alumno/listar";
    }
    
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
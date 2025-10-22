package controllers;

import models.*;
import repositories.*;
import services.AlumnoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
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
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Curso.class, "curso", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty() || text.equals("0")) {
                    setValue(null);
                } else {
                    Curso curso = new Curso();
                    curso.setId(Long.parseLong(text));
                    setValue(curso);
                }
            }
        });
        
        binder.registerCustomEditor(Empresa.class, "empresa", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty() || text.equals("0")) {
                    setValue(null);
                } else {
                    Empresa empresa = new Empresa();
                    empresa.setId(Long.parseLong(text));
                    setValue(empresa);
                }
            }
        });
        
        binder.registerCustomEditor(TutorPracticas.class, "tutorPracticas", new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty() || text.equals("0")) {
                    setValue(null);
                } else {
                    TutorPracticas tutor = new TutorPracticas();
                    tutor.setId(Long.parseLong(text));
                    setValue(tutor);
                }
            }
        });
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
    public String guardar(@ModelAttribute Alumno alumno, RedirectAttributes redirectAttributes) {
        try {
            // Validar curso obligatorio
            if (alumno.getCursoId() == null || alumno.getCursoId() == 0) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un curso");
                return "redirect:/admin/alumno/nuevo";
            }

            // Buscar y asignar el curso
            Curso curso = cursoRepository.findById(alumno.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            alumno.setCurso(curso);

            // Buscar y asignar la empresa si existe
            if (alumno.getEmpresaId() != null && alumno.getEmpresaId() > 0) {
                Empresa empresa = empresaRepository.findById(alumno.getEmpresaId())
                        .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                alumno.setEmpresa(empresa);
            } else {
                alumno.setEmpresa(null);
            }

            // Buscar y asignar el tutor si existe
            if (alumno.getTutorPracticasId() != null && alumno.getTutorPracticasId() > 0) {
                TutorPracticas tutor = tutorPracticasRepository.findById(alumno.getTutorPracticasId())
                        .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
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
   //****************************************************************************************** 
    /*
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alumno alumno, RedirectAttributes redirectAttributes) {
        try {
            // Validar curso obligatorio
        	
        	System.out.println("Curso recibido: " + alumno.getCurso());
        	if (alumno.getCurso() != null) {
        	    System.out.println("Curso ID recibido: " + alumno.getCurso().getId());
        	} else {
        	    System.out.println("Curso es null");
        	}

        	if (alumno.getCurso() == null || alumno.getCurso().getId() == null || alumno.getCurso().getId() == 0) {
            	
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un curso");
                 return "redirect:/admin/alumno/nuevo";
            }
            
            // Limpiar empresa si no se seleccionó
            if (alumno.getEmpresa() != null && (alumno.getEmpresa().getId() == null || alumno.getEmpresa().getId() == 0)) {
                alumno.setEmpresa(null);
            }
            
            // Limpiar tutor si no se seleccionó
            if (alumno.getTutorPracticas() != null && (alumno.getTutorPracticas().getId() == null || alumno.getTutorPracticas().getId() == 0)) {
                alumno.setTutorPracticas(null);
            }
            
            // Guardar a través del servicio que maneja la persistencia correctamente
            alumnoService.guardar(alumno);
            
            redirectAttributes.addFlashAttribute("success", 
                alumno.getId() == null ? "Alumno creado exitosamente" : "Alumno actualizado exitosamente");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el alumno: " + e.getMessage());
            e.printStackTrace();
            
            // Si hay error, volver al formulario correspondiente
            if (alumno.getId() != null) {
                return "redirect:/admin/alumno/editar/" + alumno.getId();
            }
            
            
            return "redirect:/admin/alumno/nuevo";
        }
        return "redirect:/admin/alumno/listar";
    }
    
    //*****************************************************************************************
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
    */
   
    
    
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
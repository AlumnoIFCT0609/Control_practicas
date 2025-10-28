package controllers;

import models.*;
import repositories.*;
import services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
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
    
    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final AlumnoService alumnoService;
    private final CursoRepository cursoRepository;
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final EmpresaRepository empresaRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;
    
    public AlumnoController(UsuarioRepository usuarioRepository,
                          AlumnoRepository alumnoRepository,
                          AlumnoService alumnoService,
                          CursoRepository cursoRepository,
                          EmpresaRepository empresaRepository,
                          CursoService cursoService,
                          EmpresaService empresaService,
                          TutorPracticasService tutorPracticasService,
                          ObservacionDiariaRepository observacionDiariaRepository,
                          TutorPracticasRepository tutorPracticasRepository) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.alumnoService = alumnoService;
        this.cursoRepository = cursoRepository;
        this.empresaRepository = empresaRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        this.cursoService= cursoService;
        this.empresaService=empresaService;
        this.tutorPracticasService=tutorPracticasService;
        this.observacionDiariaRepository=observacionDiariaRepository;
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
        model.addAttribute("mostrarBotonNuevo", true);

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
    
    @GetMapping("/observaciondiaria")
    public String MostrarObservacionExistente(Model model) {
        model.addAttribute("ObservacionDiaria", observacionDiariaRepository.findAll());
        model.addAttribute("viewName", "admin/alumno/observaciondiaria/observaciones");
        return "layout";
    }
    
    @GetMapping("/observaciondiaria/{id}")
    public String MostrarObservacionPorId(@PathVariable Long id, Model model) {
        ObservacionDiaria observacion = observacionDiariaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                           "Observación no encontrada"));
        
        model.addAttribute("ObservacionDiaria", observacion);
        
        // ⭐ AÑADE ESTO: pasar el alumno al modelo
        if (observacion.getAlumno() != null) {
            model.addAttribute("alumno", observacion.getAlumno());
        }
        
        model.addAttribute("viewName", "admin/alumno/observaciondiaria/form");
        return "layout";
    }
    
    
    @GetMapping("/observaciondiaria/{id}/editar")
    public String EditarObservacion(@PathVariable Long id, Model model) {
        // Buscar la observación
        ObservacionDiaria observacion = observacionDiariaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                           "Observación no encontrada"));
        
        // Añadir al modelo para el formulario
        model.addAttribute("observacionDiaria", observacion);
        
        // Añadir el alumno si existe (necesario para el form.html)
        if (observacion.getAlumno() != null) {
            model.addAttribute("alumnoActual", observacion.getAlumno());
        }
        
        // Ruta de la vista (ajústala según tu estructura de carpetas)
        model.addAttribute("viewName", "admin/alumno/observaciondiaria/form");
        
        return "layout";
    }

    @PostMapping("/observaciondiaria/guardar")
    public String GuardarObservacion(@ModelAttribute ObservacionDiaria observacionDiaria, 
                                      RedirectAttributes redirectAttributes) {
        try {
            // Si es edición, mantener datos originales necesarios
            if (observacionDiaria.getId() != null) {
                ObservacionDiaria existente = observacionDiariaRepository.findById(observacionDiaria.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                
                // Mantener el alumno original
                observacionDiaria.setAlumno(existente.getAlumno());
            }
            
            // Guardar
            observacionDiariaRepository.save(observacionDiaria);
            
            redirectAttributes.addFlashAttribute("success", "Observación guardada correctamente");
            return "redirect:/admin/alumno/observaciondiaria";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/admin/alumno/observaciondiaria";
        }
    }
    @PostMapping("/observaciondiaria/{id}/eliminar")
    public String EliminarObservacion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ObservacionDiaria observacion = observacionDiariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                                               "Observación no encontrada"));
            
            observacionDiariaRepository.delete(observacion);
            
            redirectAttributes.addFlashAttribute("success", "Observación eliminada correctamente");
            return "redirect:/admin/alumno/observaciondiaria";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
            return "redirect:/admin/alumno/observaciondiaria";
        }
    }
    
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // Cargar primero las listas
        List<Curso> cursos = cursoService.listarTodos();
        List<Empresa> empresas = empresaService.listarTodas();
        List<TutorPracticas> tutores = tutorPracticasService.listarTodos();
        
        // Después cargar y modificar el alumno
        Alumno alumno = alumnoService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        if (alumno.getCurso() != null) {
            alumno.setCursoId(alumno.getCurso().getId());
        }
        if (alumno.getEmpresa() != null) {
            alumno.setEmpresaId(alumno.getEmpresa().getId());
        }
        if (alumno.getTutorPracticas() != null) {
            alumno.setTutorPracticasId(alumno.getTutorPracticas().getId());
        }
        System.out.println("fechaNacimiento controlador = " + alumno.getFechaNacimiento());

        model.addAttribute("alumno", alumno);
        model.addAttribute("cursos", cursos);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutores", tutores);
        model.addAttribute("viewName", "admin/alumno/form");
        return "layout";

        //return "admin/alumno/form";
    }
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Alumno alumno, RedirectAttributes redirectAttributes) {
        try {
            // Validar curso obligatorio
            if (alumno.getCursoId() == null || alumno.getCursoId() == 0) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un curso");
                return "redirect:/admin/alumno/nuevo";
            }

            // ✅ CARGAR el curso desde la BD (no crear uno nuevo)
            Curso curso = cursoRepository.findById(alumno.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
            alumno.setCurso(curso);

            // ✅ CARGAR la empresa desde la BD si existe
            if (alumno.getEmpresaId() != null && alumno.getEmpresaId() > 0) {
                Empresa empresa = empresaRepository.findById(alumno.getEmpresaId())
                        .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                alumno.setEmpresa(empresa);
            } else {
                alumno.setEmpresa(null);
            }

            // ✅ CARGAR el tutor desde la BD si existe
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
    @GetMapping("/crear-usuario/{id}")
    public String crearUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = alumnoService.crearUsuarioParaAlumno(id);
            redirectAttributes.addFlashAttribute("success", 
                "Usuario creado exitosamente. Email: " + usuario.getEmail() + 
                " | Password inicial: DNI del alumno");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear el usuario: " + e.getMessage());
        }
        return "redirect:/admin/alumno/listar";
    }
    
    
    
}
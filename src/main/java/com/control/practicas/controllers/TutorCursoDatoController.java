package com.control.practicas.controllers;


//import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;
//import com.control.practicas.services.TutorCursoService;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.Evaluacion;
import com.control.practicas.models.ObservacionDiaria;
//import com.control.practicas.models.Usuario;
import com.control.practicas.repositories.AlumnoRepository;
import com.control.practicas.repositories.CursoRepository;
import com.control.practicas.repositories.EmpresaRepository;
import com.control.practicas.repositories.EvaluacionRepository;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import com.control.practicas.repositories.TutorCursoRepository;
import com.control.practicas.repositories.TutorPracticasRepository;
//import com.control.practicas.repositories.UsuarioRepository;
//import com.control.practicas.services.AlumnoService;
//import com.control.practicas.services.CursoService;
//import com.control.practicas.services.EmpresaService;
import com.control.practicas.services.ObservacionDiariaService;
import com.control.practicas.services.TutorPracticasService;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import java.time.LocalDate;
//import java.util.Optional;
import java.util.Map;



@Controller
@RequestMapping("/tutorcurso")
public class TutorCursoDatoController {
   //private final TutorCursoService tutorCursoService;
    private final TutorCursoRepository tutorCursoRepository;
    private final ObservacionDiariaRepository observacionDiariaRepository;
    private final ObservacionDiariaService observacionDiariaService;
    private final AlumnoRepository alumnoRepository;
    //private final AlumnoService alumnoService;
   // private final UsuarioRepository usuarioRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    //private final TutorPracticasService tutorPracticasService;
    
    
    public TutorCursoDatoController(
            ObservacionDiariaRepository observacionDiariaRepository,
            ObservacionDiariaService observacionDiariaService,
            AlumnoRepository alumnoRepository,
            EvaluacionRepository evaluacionRepository,
           // TutorCursoService tutorCursoService,
           // AlumnoService alumnoService,
            CursoRepository cursoRepository,
            TutorCursoRepository tutorCursoRepository,
            EmpresaRepository empresaRepository,
           // UsuarioRepository usuarioRepository,
            TutorPracticasRepository tutorPracticasRepository,
            TutorPracticasService tutorPracticasService) {
    	//this.tutorCursoService = tutorCursoService;
        this.observacionDiariaRepository = observacionDiariaRepository;
        this.observacionDiariaService = observacionDiariaService;
        this.evaluacionRepository=evaluacionRepository;
        this.alumnoRepository = alumnoRepository;
       // this.alumnoService = alumnoService;
       // this.usuarioRepository = usuarioRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        this.cursoRepository=cursoRepository;
        this.empresaRepository=empresaRepository;
        //this.tutorPracticasService=tutorPracticasService;
        this.tutorCursoRepository=tutorCursoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // 1️⃣ Obtener el tutor actualmente autenticado usando tu método auxiliar
        TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);

        // 2️⃣ Obtener los cursos que tienen alumnos de este tutor
        List<Curso> cursosDelTutor = cursoRepository.findDistinctByTutorCurso_Id(tutorCurso.getId());


        // 3️⃣ Obtener los alumnos de esos cursos
        List<Alumno> alumnosDelTutor = alumnoRepository.findByCursoIn(cursosDelTutor);

        // 4️⃣ Obtener los tutores de prácticas asociados a esos alumnos
        List<TutorPracticas> tutoresPracticasDelTutor = ((Stream<TutorPracticas>) alumnosDelTutor.stream()
                .map(Alumno::getTutorPracticas)
                .filter(Objects::nonNull)
                .distinct())
        		.collect(Collectors.toList());

        // 5️⃣ Obtener las empresas de los alumnos de esos cursos
        List<Empresa> empresasDelTutor = alumnosDelTutor.stream()
                .map(Alumno::getEmpresa)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 6️⃣ Contar alumnos por curso
        Map<Long, Long> alumnosPorCurso = cursosDelTutor.stream()
                .collect(Collectors.toMap(
                        Curso::getId,
                        curso -> alumnosDelTutor.stream()
                                .filter(a -> a.getCurso() != null && a.getCurso().getId().equals(curso.getId()))
                                .count()
                ));

        // 7️⃣ Obtener las evaluaciones de los alumnos del tutor de curso
        List<Evaluacion> evaluacionesDelTutor = evaluacionRepository.findByTutorCursoId(tutorCurso.getId());

        // 8️⃣ Pasar todos los datos al modelo
        model.addAttribute("tutorcurso", tutorCurso);
        model.addAttribute("cursos", cursosDelTutor);
        model.addAttribute("alumnos", alumnosDelTutor);
        model.addAttribute("empresa", empresasDelTutor);
        model.addAttribute("tutorp", tutoresPracticasDelTutor);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("evaluaciones", evaluacionesDelTutor);  // <-- Añadid
        model.addAttribute("pageTitle", "Dashboard Tutor Curso");
        model.addAttribute("viewName", "tutorcurso/dashboard");

        return "layout"; // tu plantilla base
    }


 // En tu TutorCursoController

    @GetMapping("/alumno")
    public String listarAlumnosDelTutorCurso(Model model,
                                              Authentication authentication,
                                              RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            
            // Obtener todos los cursos asignados a este tutor
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());
            
            // Obtener todos los alumnos de esos cursos
            List<Alumno> alumnosDelCurso;
            if (cursosDelTutor.isEmpty()) {
                alumnosDelCurso = new ArrayList<>();
            } else {
                // Extraer los IDs de los cursos
                List<Long> cursoIds = cursosDelTutor.stream()
                    .map(Curso::getId)
                    .collect(Collectors.toList());
                
                alumnosDelCurso = alumnoRepository.findByCursoIds(cursoIds);
            }
            
            model.addAttribute("usuario", authentication);
            model.addAttribute("alumnos", alumnosDelCurso);
            model.addAttribute("mostrarBotonNuevo", false);
            model.addAttribute("alumnosDelCurso", alumnosDelCurso);
            model.addAttribute("tutorCursoActual", tutorCurso);
            model.addAttribute("cursosDelTutor", cursosDelTutor);
            model.addAttribute("viewName", "admin/alumno/listar");
            
            return "layout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cargar los alumnos: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/tutorcurso/dashboard";
        }
    }

    @GetMapping("/alumno/ver/{id}")
    public String verAlumno(@PathVariable Long id,
                            Model model,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
            
            // Verificar que el alumno pertenece a alguno de los cursos del tutor
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());
            boolean perteneceAlTutor = cursosDelTutor.stream()
                .anyMatch(curso -> curso.getId().equals(alumno.getCurso().getId()));
            
            if (!perteneceAlTutor) {
                redirectAttributes.addFlashAttribute("error", 
                    "No tienes permiso para ver este alumno");
                return "redirect:/tutorcurso/alumno";
            }
            

            model.addAttribute("alumno", alumno);
            model.addAttribute("cursos", cursosDelTutor);
            model.addAttribute("empresas", empresaRepository.findAll());
            model.addAttribute("tutores", tutorPracticasRepository.findAll());
            model.addAttribute("esTutorCurso", true);
            model.addAttribute("esVistaAlumno", false);
            model.addAttribute("viewName", "admin/alumno/form");
            
            return "layout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cargar el alumno: " + e.getMessage());
            return "redirect:/tutorcurso/alumno";
        }
    }

   /* @GetMapping("/alumno/editar/{id}")
    public String editarAlumno(@PathVariable Long id,
                               Model model,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
            
            // Verificar que el alumno pertenece a alguno de los cursos del tutor
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());
            boolean perteneceAlTutor = cursosDelTutor.stream()
                .anyMatch(curso -> curso.getId().equals(alumno.getCurso().getId()));
            
            if (!perteneceAlTutor) {
                redirectAttributes.addFlashAttribute("error", 
                    "No tienes permiso para editar este alumno");
                return "redirect:/tutorcurso/alumno";
            }
            
            model.addAttribute("alumno", alumno);
            model.addAttribute("cursos", cursosDelTutor);
            model.addAttribute("empresas", empresaRepository.findAll());
            model.addAttribute("tutores", tutorPracticasRepository.findAll());
            model.addAttribute("esTutorCurso", true);
            model.addAttribute("esVistaAlumno", false);
            model.addAttribute("viewName", "admin/alumno/form");
            
            return "layout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cargar el alumno: " + e.getMessage());
            return "redirect:/tutorcurso/alumno";
        }
    }*/
    @GetMapping("/alumno/editar/{id}")
    public String editarAlumno(@PathVariable Long id,
                               Model model,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

            // Verificar que el alumno pertenece a alguno de los cursos del tutor
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());
            boolean perteneceAlTutor = cursosDelTutor.stream()
                .anyMatch(curso -> curso.getId().equals(alumno.getCurso().getId()));

            if (!perteneceAlTutor) {
                redirectAttributes.addFlashAttribute("error",
                    "No tienes permiso para editar este alumno");
                return "redirect:/tutorcurso/alumno";
            }

            // Lista con el tutor de prácticas del alumno
            List<TutorPracticas> tutorPracticas = alumno.getTutorPracticas() != null 
                ? List.of(alumno.getTutorPracticas()) 
                : List.of();

            model.addAttribute("alumno", alumno);
            model.addAttribute("cursos", cursosDelTutor);
            model.addAttribute("empresas", empresaRepository.findAll());
            model.addAttribute("tutorPracticas", tutorPracticas);  // <-- Cambio aquí
            model.addAttribute("modo", "editar");  // <-- Añadido para el formulario
            model.addAttribute("esTutorCurso", true);
            model.addAttribute("esVistaAlumno", false);
            model.addAttribute("viewName", "admin/alumno/form");

            return "layout";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cargar el alumno: " + e.getMessage());
            return "redirect:/tutorcurso/alumno";
        }
    }

    @PostMapping("/alumno/guardar")
    public String guardarAlumno(@ModelAttribute Alumno alumno,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());
            
            // Verificar que el alumno pertenece a alguno de los cursos del tutor
            if (alumno.getId() != null) {
                Alumno alumnoExistente = alumnoRepository.findById(alumno.getId())
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
                
                boolean perteneceAlTutor = cursosDelTutor.stream()
                    .anyMatch(curso -> curso.getId().equals(alumnoExistente.getCurso().getId()));
                
                if (!perteneceAlTutor) {
                    redirectAttributes.addFlashAttribute("error", 
                        "No tienes permiso para editar este alumno");
                    return "redirect:/tutorcurso/alumno";
                }
                
                // Mantener el curso original (no permitir cambio)
                alumno.setCurso(alumnoExistente.getCurso());
            }
            
            // Manejar relaciones
            if (alumno.getCursoId() != null) {
                Curso curso = cursoRepository.findById(alumno.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
                alumno.setCurso(curso);
            }
            
            if (alumno.getEmpresaId() != null) {
                Empresa empresa = empresaRepository.findById(alumno.getEmpresaId())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
                alumno.setEmpresa(empresa);
            }
            
            if (alumno.getTutorPracticasId() != null) {
                TutorPracticas tutor = tutorPracticasRepository.findById(alumno.getTutorPracticasId())
                    .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
                alumno.setTutorPracticas(tutor);
            }
            
            alumnoRepository.save(alumno);
            redirectAttributes.addFlashAttribute("success", 
                "Alumno guardado correctamente");
            
            return "redirect:/tutorcurso/alumno";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tutorcurso/alumno";
        }
    }

    
    // *************************************************************************************
    @GetMapping("/cursos")
    public String listarCursosDelTutor(Model model,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso_Id(tutorCurso.getId());

            model.addAttribute("usuario", authentication);
            model.addAttribute("cursos", cursosDelTutor);
            model.addAttribute("tutorCursoActual", tutorCurso);
            model.addAttribute("mostrarBotonNuevo", false);
            model.addAttribute("viewName", "admin/curso/listar");

            return "layout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar los cursos: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/tutorcurso/dashboard";
        }
    }

    // ============================
    // VER CURSO (solo lectura)
    // ============================

    @GetMapping("/cursos/ver/{id}")
    public String verCurso(@PathVariable Long id,
                           Model model,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            Curso curso = cursoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            // Verificar que el curso pertenece al tutor
            if (!curso.getTutorCurso().getId().equals(tutorCurso.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver este curso");
                return "redirect:/tutorcurso/curso";
            }

            model.addAttribute("curso", curso);
            model.addAttribute("tutorCursoActual", tutorCurso);
            model.addAttribute("esTutorCurso", true);
            model.addAttribute("esVistaCurso", true);
            model.addAttribute("viewName", "admin/curso/form");

            return "layout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el curso: " + e.getMessage());
            return "redirect:/tutorcurso/cursos";
        }
    }

    // ============================
    // EDITAR CURSO
    // ============================

    @GetMapping("/cursos/editar/{id}")
    public String editarCurso(@PathVariable Long id,
                              Model model,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            Curso curso = cursoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

            // Verificar propiedad
            if (!curso.getTutorCurso().getId().equals(tutorCurso.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este curso");
                return "redirect:/tutorcurso/curso";
            }

            model.addAttribute("curso", curso);
            model.addAttribute("tutores", List.of(tutorCurso));
            model.addAttribute("tutorCursoActual", tutorCurso);
            model.addAttribute("esTutorCurso", true);
            model.addAttribute("esVistaCurso", true);
            model.addAttribute("viewName", "admin/curso/form");

            return "layout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al editar el curso: " + e.getMessage());
            return "redirect:/tutorcurso/cursos";
        }
    }

    // ============================
    // GUARDAR CURSO (solo lo suyo)
    // ============================

    @PostMapping("/cursos/guardar")
    public String guardarCurso(@ModelAttribute Curso curso,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);

            // Validar propiedad del curso (si existe)
            if (curso.getId() != null) {
                Curso existente = cursoRepository.findById(curso.getId())
                        .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
                if (!existente.getTutorCurso().getId().equals(tutorCurso.getId())) {
                    redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar este curso");
                    return "redirect:/tutorcurso/curso";
                }
            }

            // Asociar el curso al tutor autenticado
            curso.setTutorCurso(tutorCurso);
            cursoRepository.save(curso);

            redirectAttributes.addFlashAttribute("success", "Curso guardado correctamente");
            return "redirect:/tutorcurso/cursos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el curso: " + e.getMessage());
            return "redirect:/tutorcurso/cursos";
        }
    }
    //*************************************************************************************

    
    @GetMapping("/observaciondiaria/listar")
    public String listar(
            @RequestParam(required = false) Long alumnoId,
            Model model, 
            Authentication authentication) {
        
        try {
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
            List<Curso> cursosDelTutor = cursoRepository.findByTutorCurso(tutorCurso);
            List<Alumno> alumnosDelTutorCurso = alumnoRepository.findByCursoIn(cursosDelTutor);

            List<ObservacionDiaria> observaciones;
            Alumno alumnoSeleccionado = null;
            TutorPracticas tutorPracticas = null;

            if (alumnoId != null) {
                alumnoSeleccionado = alumnoRepository.findById(alumnoId)
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

                if (!tutorCurso.getId().equals(tutorCurso.getId())) {
                    throw new RuntimeException("No tiene permisos para ver esta observación");
                }

                observaciones = observacionDiariaService.listarPorAlumnoOrdenadas(alumnoId);
                tutorPracticas = alumnoSeleccionado.getTutorPracticas(); // <- ahora se saca del alumno
            } else {
                if (alumnosDelTutorCurso.isEmpty()) {
                    observaciones = List.of();
                } else {
                    observaciones = observacionDiariaRepository.findByAlumnoInOrderByFechaDesc(alumnosDelTutorCurso);
                    // Si quieres, podrías tomar el tutorPracticas del primer alumno o dejarlo null
                }
            }

            model.addAttribute("ObservacionDiaria", observaciones);
            model.addAttribute("alumnosDelTutorCurso", alumnosDelTutorCurso);
            model.addAttribute("alumnoSeleccionado", alumnoSeleccionado);
            model.addAttribute("tutorActual", tutorPracticas); // <- ahora es el tutor del alumno
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
        TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);
        
        ObservacionDiaria observacionDiaria = observacionDiariaService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Observación no encontrada"));

        if (!observacionDiaria.getAlumno().getCurso().getTutorCurso().getId().equals(tutorCurso.getId())) {
            throw new RuntimeException("No tiene permisos para ver esta observación");
        }


        TutorPracticas tutorPracticas = observacionDiaria.getAlumno().getTutorPracticas();

        model.addAttribute("observacionDiaria", observacionDiaria);
        model.addAttribute("alumnoActual", observacionDiaria.getAlumno());
        model.addAttribute("tutorActual", tutorPracticas); // <- tutor del alumno
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
            TutorCurso tutorCurso = getTutorCursoAutenticado(authentication);

            ObservacionDiaria observacionExistente = observacionDiariaService.buscarPorId(observacionDiaria.getId())
                .orElseThrow(() -> new RuntimeException("Observación no encontrada"));

            if (!tutorCurso.getId().equals(tutorCurso.getId())) {
                throw new RuntimeException("No tiene permisos para ver esta observación");
            }

            observacionExistente.setObservacionesTutor(observacionesTutor);
            observacionDiariaService.guardar(observacionExistente);
            redirectAttributes.addFlashAttribute("success", "Observaciones del tutor actualizadas exitosamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar las observaciones: " + e.getMessage());
            e.printStackTrace();
        }
       // return "redirect:/admin/alumno/observaciondiaria/observaciones";
        return "redirect:/tutorcurso/observaciondiaria/listar";
    }


    
    
    // *************************************************************************************
    // Método helper para obtener el tutor curso autenticado
    private TutorCurso getTutorCursoAutenticado(Authentication authentication) {
    	// Muestra información de depuración
        System.out.println("=== AUTENTICACIÓN ACTUAL ===");
        System.out.println("Usuario autenticado: " + authentication.getName());
        System.out.println("Roles asignados:");
        
        
        authentication.getAuthorities().forEach(a -> System.out.println(" - " + a.getAuthority()));
        String email = authentication.getName();
        return tutorCursoRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email no encontrado"));
    }

}

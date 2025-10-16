package controllers;

import models.*;
import repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tutor-curso")
public class TutorCursoController {

    private final UserRepository userRepository;
    private final TutorCursoRepository tutorCursoRepository;
    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;

    public TutorCursoController(UserRepository userRepository,
                                TutorCursoRepository tutorCursoRepository,
                                CursoRepository cursoRepository,
                                AlumnoRepository alumnoRepository) {
        this.userRepository = userRepository;
        this.tutorCursoRepository = tutorCursoRepository;
        this.cursoRepository = cursoRepository;
        this.alumnoRepository = alumnoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());

        List<Curso> cursos = List.of();
        List<Alumno> alumnos = List.of();
        Set<Empresa> empresas = Set.of();
        Set<TutorPracticas> tutoresPracticas = Set.of();
        long alumnosConPracticas = 0;

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getReferenceId() != null) {
                Optional<TutorCurso> tutorOpt = tutorCursoRepository.findById(user.getReferenceId());
                if (tutorOpt.isPresent()) {
                    TutorCurso tutor = tutorOpt.get();
                    model.addAttribute("tutor", tutor);

                    // Obtener cursos del tutor
                    cursos = cursoRepository.findAll(); // TODO: Ajustar con findByTutorCurso(tutor) si existe
                    
                    // Obtener alumnos de esos cursos
                    alumnos = alumnoRepository.findAll(); // TODO: Ajustar con findByCursoIn(cursos) si existe
                    
                    // Obtener empresas únicas
                    empresas = alumnos.stream()
                        .map(Alumno::getEmpresa)
                        .filter(e -> e != null)
                        .map(e -> (Empresa) e)
                        .collect(Collectors.toSet());
                    
                    // Obtener tutores de prácticas únicos
                    tutoresPracticas = alumnos.stream()
                        .map(Alumno::getTutorPracticas)
                        .filter(t -> t != null)
                        .collect(Collectors.toSet());
                    
                    // Contar alumnos que tienen empresa asignada (están en prácticas)
                    alumnosConPracticas = alumnos.stream()
                        .filter(a -> a.getEmpresa() != null)
                        .count();
                }
            }
        }

        // Atributos para el dashboard
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnos", alumnos);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutoresPracticas", tutoresPracticas);
        model.addAttribute("alumnosActivos", alumnosConPracticas); // Alumnos con empresa = en prácticas activas
        
        // Para el layout
        model.addAttribute("pageTitle", "Dashboard - Tutor de Curso");
        model.addAttribute("viewName", "tutor-curso/dashboard");
        
        return "layout";
    }
}
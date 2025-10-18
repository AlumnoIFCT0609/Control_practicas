package controllers;

import models.*;
import repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tutor-practicas")
public class TutorPracticasController {
    
    private final UserRepository userRepository;
    private final TutorPracticasRepository tutorPracticasRepository;
    //private final AlumnoRepository alumnoRepository;
    
    public TutorPracticasController(UserRepository userRepository,
                                   TutorPracticasRepository tutorPracticasRepository,
                                   AlumnoRepository alumnoRepository) {
        this.userRepository = userRepository;
        this.tutorPracticasRepository = tutorPracticasRepository;
        //this.alumnoRepository = alumnoRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Optional<Usuario> userOpt = userRepository.findByEmail(auth.getName());
        
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            
            // Obtener el tutor de prácticas usando el referenceId
            if (usuario.getReferenceId() != null) {
                Optional<TutorPracticas> tutorOpt = tutorPracticasRepository.findById(usuario.getReferenceId());
                if (tutorOpt.isPresent()) {
                    TutorPracticas tutor = tutorOpt.get();
                    model.addAttribute("tutor", tutor);
                    model.addAttribute("empresa", tutor.getEmpresa());
                }
            }
        }
        
        // Obtener todos los alumnos (luego se puede filtrar por tutor)
        //List<Alumno> alumnos = alumnoRepository.findAll();
        //model.addAttribute("alumnos", alumnos);

        model.addAttribute("pageTitle", "Dashboard - Tutor de Practicas");
        model.addAttribute("viewName", "tutor-practicas/dashboard");
        
        return "layout";
        
        
        //return "tutor-practicas/dashboard";
    }
}

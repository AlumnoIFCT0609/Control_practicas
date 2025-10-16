package controllers;

import models.*;
import repositories.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/alumno")
public class AlumnoController {
    
    private final UserRepository userRepository;
    private final AlumnoRepository alumnoRepository;
    
    public AlumnoController(UserRepository userRepository,
                          AlumnoRepository alumnoRepository) {
        this.userRepository = userRepository;
        this.alumnoRepository = alumnoRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Obtener el alumno usando el referenceId
            if (user.getReferenceId() != null) {
                Optional<Alumno> alumnoOpt = alumnoRepository.findById(user.getReferenceId());
                if (alumnoOpt.isPresent()) {
                    Alumno alumno = alumnoOpt.get();
                    model.addAttribute("alumno", alumno);
                    model.addAttribute("curso", alumno.getCurso());
                    model.addAttribute("empresa", alumno.getEmpresa());
                    model.addAttribute("tutorPracticas", alumno.getTutorPracticas());
                    model.addAttribute("viewName", "alumno/dashboard");
                }
            }
        }
        //return "layout";
        return "alumno/dashboard";
    }
}

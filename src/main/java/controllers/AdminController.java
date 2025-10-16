package controllers;

//import models.*;
//import services.*;
import repositories.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserRepository userRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaRepository empresaRepository;
    private final AlumnoRepository alumnoRepository;
    
    public AdminController(UserRepository userRepository, 
                          CursoRepository cursoRepository,
                          EmpresaRepository empresaRepository,
                          AlumnoRepository alumnoRepository) {
        this.userRepository = userRepository;
        this.cursoRepository = cursoRepository;
        this.empresaRepository = empresaRepository;
        this.alumnoRepository = alumnoRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("usuarios", userRepository.findAll());
        model.addAttribute("cursos", cursoRepository.findAll());
        model.addAttribute("empresas", empresaRepository.findAll());
        model.addAttribute("alumnos", alumnoRepository.findAll());
        model.addAttribute("pageTitle", "Dashboard - Administrador");
        model.addAttribute("viewName", "admin/dashboard");
        //model.addAttribute("content", "admin/dashboard");
        return "layout";
    }
}
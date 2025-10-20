package controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import models.TutorCurso;
import services.TutorCursoService;

import java.util.List;

@Controller
public class DashboardTutorCursoController {

    private final TutorCursoService tutorCursoService;

    public DashboardTutorCursoController(TutorCursoService tutorCursoService) {
        this.tutorCursoService = tutorCursoService;
    }

    @GetMapping("/tutorcurso/dashboard")
    public String dashboard(Model model) {
        List<TutorCurso> tutores = tutorCursoService.listarTodos();
        model.addAttribute("tutores", tutores);
        model.addAttribute("pageTitle", "Dashboard Tutor Curso");
        model.addAttribute("viewName", "tutorcurso/dashboard"); // Para que tu layout lo incluya
        return "layout";  // O el nombre del template principal que uses
    }
}

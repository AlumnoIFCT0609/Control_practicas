package com.control.practicas.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.control.practicas.models.TutorPracticas;
import com.control.practicas.repositories.TutorPracticasRepository;
import com.control.practicas.services.TutorPracticasService;

@Controller
public class DashboardTutorPracticasController {


    private final TutorPracticasService tutorPracticasService;
    private final TutorPracticasRepository tutorPracticasRepository;

    public DashboardTutorPracticasController(TutorPracticasService tutorPracticasService, TutorPracticasRepository tutorPracticasRepository) {
        	this.tutorPracticasService = tutorPracticasService;
        	this.tutorPracticasRepository= tutorPracticasRepository;
    }

    @GetMapping("/tutorpracticas/dashboard")
    public String dashboard(Model model) {
        List<TutorPracticas> tutores = tutorPracticasService.listarTodos();
        model.addAttribute("tutores", tutores);
        model.addAttribute("pageTitle", "Dashboard Tutor Practicas");
        model.addAttribute("viewName", "tutorpracticas/dashboard"); // Para que tu layout lo incluya
        return "layout";  // O el nombre del template principal que uses
    }
}
package controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import models.Alumno;
import repositories.AlumnoRepository;
import services.AlumnoService;


	@Controller
	public class DashboardAlumnoController {


	    private final AlumnoService alumnoService;
	    private final AlumnoRepository alumnoRepository;

	    public DashboardAlumnoController(AlumnoService alumnoService, AlumnoRepository alumnoRepository) {
	        	this.alumnoService = alumnoService;
	        	this.alumnoRepository= alumnoRepository;
	    }

	    @GetMapping("/alumno/dashboard")
	    public String dashboard(Model model) {
	        List<Alumno> alumnos = alumnoService.listarTodos();
	        model.addAttribute("alumnos", alumnos);
	        model.addAttribute("pageTitle", "Dashboard Alumno");
	        model.addAttribute("viewName", "alumno/dashboard"); // Para que tu layout lo incluya
	        return "layout";  // O el nombre del template principal que uses
	    }
	}
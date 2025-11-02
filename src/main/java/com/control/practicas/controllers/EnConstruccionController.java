package com.control.practicas.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//import com.control.practicas.models.Alumno;
//import com.control.practicas.services.AlumnoService;

@Controller
@RequestMapping("/enconstruccion")
public class EnConstruccionController {

	    @GetMapping
	    public String enConstruccion(Model model, Authentication authentication) {
	        // Obtener datos básicos del usuario, opcional si quieres mostrar su nombre
	        String nombreUsuario = authentication != null ? authentication.getName() : "Usuario";

	        model.addAttribute("nombreUsuario", nombreUsuario);
	        model.addAttribute("pageTitle", "Funcionalidad en construcción");
	        model.addAttribute("viewName", "enconstruccion"); // Para que tu layout lo incluya

	        return "layout";
	    }
	}



package controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import models.Alumno;
import services.AlumnoService;

@Controller
@RequestMapping("/alumno")
public class AlumnoDatoController {
    private final AlumnoService alumnoService;
    public AlumnoDatoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }
    @GetMapping("/perfil")
    public String misDatos(RedirectAttributes redirectAttrs, Authentication authentication) {
        String email = authentication.getName();
        try {
            Alumno alumno = alumnoService.obtenerPorEmail(email);
            if (alumno == null) {
                redirectAttrs.addFlashAttribute("alerta", "No se encontró ningún alumno con el email: " + email);
                return "redirect:/alumno/dashboard"; // redirige al dashboard
            }
            redirectAttrs.addFlashAttribute("alumno", alumno);
            return "redirect:/alumno/misdatos/form"; // o la vista que corresponda
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("error", "No se encontró el alumno con el email: " + email);
            return "redirect:/alumno/dashboard";
        }
    }

}

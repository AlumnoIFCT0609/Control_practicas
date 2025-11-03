package com.control.practicas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.control.practicas.models.Usuario;
import com.control.practicas.services.UsuarioService;

import java.security.Principal;




@Controller
@RequestMapping("/comun")
public class CommonController {
	 private final UsuarioService usuarioService;
	    
	    public CommonController(UsuarioService usuarioService) {
	        this.usuarioService = usuarioService;
	    }
	    
	    // Mostrar formulario de cambio de contraseña
	    @GetMapping("/cambiar-password")
	    public String mostrarFormularioCambioPassword() {
	        return "comun/cambiar-password";
	    }
	    
	    // Procesar cambio de contraseña
	    @PostMapping("/cambiar-password")
	    public String cambiarPassword(@RequestParam String passwordActual,
	                                 @RequestParam String passwordNueva,
	                                 @RequestParam String passwordConfirmacion,
	                                 RedirectAttributes redirectAttributes,
	                                 Principal principal) {
	        try {
	            // Validaciones
	            if (passwordNueva == null || passwordNueva.length() < 6) {
	                redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres");
	                return "redirect:/comun/cambiar-password";
	            }
	            
	            if (!passwordNueva.equals(passwordConfirmacion)) {
	                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
	                return "redirect:/comun/cambiar-password";
	            }
	            
	            // Obtener usuario actual por email
	            Usuario usuario = usuarioService.findByEmail(principal.getName())
	                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
	            
	            // Cambiar contraseña
	            usuarioService.cambiarPassword(usuario.getId(), passwordActual, passwordNueva);
	            
	            redirectAttributes.addFlashAttribute("success", "Contraseña cambiada exitosamente");
	            
	            // Redirigir según el rol
	            String redirectUrl;
	            switch (usuario.getRol()) {
	                case ADMIN:
	                    redirectUrl = "redirect:/admin/dashboard";
	                    break;
	                case ALUMNO:
	                    redirectUrl = "redirect:/alumno/dashboard";
	                    break;
	                case TUTOR_PRACTICAS:
	                    redirectUrl = "redirect:/tutor-practicas/dashboard";
	                    break;
	                case TUTOR_CURSO:
	                    redirectUrl = "redirect:/tutor-curso/dashboard";
	                    break;
	                default:
	                    redirectUrl = "redirect:/";
	                    break;
	            }
	            
	            return redirectUrl;
	            
	        } catch (IllegalStateException e) {
	            redirectAttributes.addFlashAttribute("error", e.getMessage());
	            return "redirect:/comun/cambiar-password";
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña");
	            e.printStackTrace();
	            return "redirect:/comun/cambiar-password";
	        }
	    }
}

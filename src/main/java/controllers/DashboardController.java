package controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_CURSO"))) {
            return "redirect:/tutor-curso/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("TUTOR_PRACTICAS"))) {
            return "redirect:/tutor-practicas/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ALUMNO"))) {
            return "redirect:/alumno/dashboard";
        }
        return "redirect:/login";
    }
    
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}

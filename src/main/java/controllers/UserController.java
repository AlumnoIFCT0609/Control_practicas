package controllers;

import models.User;
import repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

//import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

        
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    

    @GetMapping
    public String listarUsuarios(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        model.addAttribute("viewName", "admin/users/list");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        //model.addAttribute("user", new User());
    	User newUser = new User();
        /*user.setEmail("");
        user.setPassword("");
        user.setRol(null);
        user.setActivo(true); */
        model.addAttribute("nuevoUsuario", newUser);
        model.addAttribute("pageTitle", "Nuevo Usuario");
        model.addAttribute("viewName", "admin/users/nuevo");
        return "layout";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute User user) {
        if (user.getId() == null) {
            user.setFechaCreacion();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUltimoAcceso();
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Editar Usuario");
        model.addAttribute("viewName", "admin/users/form");
        return "layout";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}

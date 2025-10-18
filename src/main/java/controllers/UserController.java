package controllers;

import models.Usuario;
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
        List<Usuario> usuarios = userRepository.findAll();
        model.addAttribute("users", usuarios);
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        model.addAttribute("viewName", "admin/users/list");
        return "layout";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        //model.addAttribute("user", new User());
    	Usuario newUser = new Usuario();
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
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setFechaCreacion();
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setUltimoAcceso();
        userRepository.save(usuario);
        return "redirect:/admin/users";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", usuario);
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

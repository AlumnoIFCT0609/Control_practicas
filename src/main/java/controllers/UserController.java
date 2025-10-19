package controllers;

import models.Usuario;
import repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserController {
        
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Listar todos los usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = userRepository.findAll();
        model.addAttribute("users", usuarios);
        model.addAttribute("viewName", "admin/users/listar");
        return "layout";
    }
    
    // Mostrar formulario para crear nuevo usuario
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        Usuario newUser = new Usuario();
        newUser.setActivo(true); // Por defecto activo
        model.addAttribute("user", newUser); // ✅ Consistente: siempre "user"
        model.addAttribute("viewName", "admin/users/form");
        return "layout";
    }
    
    // Mostrar formulario para editar usuario existente
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = userRepository.findById(id);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("user", usuarioOpt.get());
            model.addAttribute("viewName", "admin/users/form");
            return "layout";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/users";
        }
    }
    
    // Guardar o actualizar usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("user") Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            // Si es nuevo usuario
            if (usuario.getId() == null) {
                usuario.setFechaCreacion();
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            } else {
                // Si es edición, solo actualizar contraseña si se proporcionó una nueva
                Usuario existente = userRepository.findById(usuario.getId()).orElseThrow();
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                } else {
                    usuario.setPassword(existente.getPassword());
                }
                usuario.setFechaCreacion(existente.getFechaCreacion());
            }
            
            usuario.setUltimoAcceso();
            userRepository.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", 
                usuario.getId() == null ? "Usuario creado exitosamente" : "Usuario actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    // Eliminar usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = userRepository.findById(id);
            if (usuarioOpt.isPresent()) {
                userRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
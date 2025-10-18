package services;


import models.Usuario;
import repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository=null;
    private final PasswordEncoder passwordEncoder=null;
    
    @Transactional
    public Usuario crearUsuario(String email, String password, Usuario.Rol rol, Long referenceId) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setReferenceId(referenceId);
        usuario.setActivo(true);
        
        return userRepository.save(usuario);
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return userRepository.findById(id);
    }
    
    public List<Usuario> listarTodos() {
        return userRepository.findAll();
    }
    
    public List<Usuario> listarActivos() {
        return userRepository.findByActivoTrue();
    }
    
    public List<Usuario> listarPorRol(Usuario.Rol rol) {
        return userRepository.findByRol(rol);
    }
    
    @Transactional
    public void cambiarPassword(Long userId, String newPassword) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(usuario);
    }
    
    @Transactional
    public void activarDesactivar(Long userId, boolean activo) {
        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(activo);
        userRepository.save(usuario);
    }
    
    @Transactional
    public void eliminar(Long userId) {
        userRepository.deleteById(userId);
    }
}

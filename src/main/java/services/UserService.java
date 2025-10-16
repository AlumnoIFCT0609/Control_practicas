package services;


import models.User;
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
    public User crearUsuario(String email, String password, User.Rol rol, Long referenceId) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol);
        user.setReferenceId(referenceId);
        user.setActivo(true);
        
        return userRepository.save(user);
    }
    
    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> listarTodos() {
        return userRepository.findAll();
    }
    
    public List<User> listarActivos() {
        return userRepository.findByActivoTrue();
    }
    
    public List<User> listarPorRol(User.Rol rol) {
        return userRepository.findByRol(rol);
    }
    
    @Transactional
    public void cambiarPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Transactional
    public void activarDesactivar(Long userId, boolean activo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActivo(activo);
        userRepository.save(user);
    }
    
    @Transactional
    public void eliminar(Long userId) {
        userRepository.deleteById(userId);
    }
}

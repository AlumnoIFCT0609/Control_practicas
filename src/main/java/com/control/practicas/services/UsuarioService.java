package com.control.practicas.services;


//import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Usuario;
import com.control.practicas.models.Usuario.Rol;
import com.control.practicas.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioService(UsuarioRepository usuarioRepository, 
            PasswordEncoder passwordEncoder) {
    			this.usuarioRepository = usuarioRepository;
    			this.passwordEncoder = passwordEncoder;
    		}
    
    
    
    
    @Transactional
    public Usuario crearUsuario(String email, String password, Usuario.Rol rol, Long referenceId) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }
         Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setReferenceId(referenceId);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);
        
        return usuarioRepository.save(usuario);
        
        }
        
        public Usuario crearUsuarioParaEntidad(String email, String dni, 
                Rol rol, Long referenceId, 
                boolean activo) {
        		// Verificar si ya existe un usuario con ese email
        		if (usuarioRepository.findByEmail(email).isPresent()) {
        				throw new IllegalStateException("Ya existe un usuario con el email: " + email);
        			}
        		Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setPassword(passwordEncoder.encode(dni)); // DNI como password inicial
                usuario.setRol(rol);
                usuario.setReferenceId(referenceId);
                usuario.setActivo(activo);
                usuario.setFechaCreacion(LocalDateTime.now());

                
                return usuarioRepository.save(usuario);
        
       
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }
    
    public List<Usuario> listarPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    @Transactional
    public void cambiarPassword(Long userId, String newPassword) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void activarDesactivar(Long userId, boolean activo) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void eliminar(Long userId) {
        usuarioRepository.deleteById(userId);
    }

	public boolean existeUsuarioPorEmail(String email) {
		return usuarioRepository.findByEmail(email).isPresent();	
	}
}

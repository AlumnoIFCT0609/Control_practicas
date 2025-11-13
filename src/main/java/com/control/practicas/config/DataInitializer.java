package com.control.practicas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.control.practicas.models.Usuario;
import com.control.practicas.repositories.UsuarioRepository;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando verificacion de base de datos y datos iniciales...");
        
        // Usuarios de prueba originales
        createUserIfNotExists("admin@controlpracticas.com", "admin123", Usuario.Rol.ADMIN, 0, "administrador de prueba");
        createUserIfNotExists("alumno@controlpracticas.com", "alumno123", Usuario.Rol.ALUMNO, 8, "alumno de prueba");
        createUserIfNotExists("tutor@controlpracticas.com", "tutor123", Usuario.Rol.TUTOR_CURSO, 5, "tutor de prueba");
        createUserIfNotExists("practicas@controlpracticas.com", "practicas123", Usuario.Rol.TUTOR_PRACTICAS, 5, "tutor de prácticas de prueba");
        
        // Usuarios reales del sistema
        createUserIfNotExists("admin@practicas.edu", "admin123", Usuario.Rol.ADMIN, 1, "administrador");
        createUserIfNotExists("ana.martinez@instituto.edu", "tutor123", Usuario.Rol.TUTOR_CURSO, 1, "tutor de curso Ana Martínez");
        createUserIfNotExists("pedro.sanchez@instituto.edu", "tutor123", Usuario.Rol.TUTOR_CURSO, 2, "tutor de curso Pedro Sánchez");
        createUserIfNotExists("luis.gonzalez@techsolutions.com", "tutor123", Usuario.Rol.TUTOR_PRACTICAS, 1, "tutor de prácticas Luis González");
        createUserIfNotExists("laura.rodriguez@marketingpro.com", "tutor123", Usuario.Rol.TUTOR_PRACTICAS, 2, "tutor de prácticas Laura Rodríguez");
        createUserIfNotExists("juan.perez@estudiante.edu", "alumno123", Usuario.Rol.ALUMNO, 1, "alumno Juan Pérez");
        createUserIfNotExists("maria.lopez@estudiante.edu", "alumno123", Usuario.Rol.ALUMNO, 2, "alumno María López");
        createUserIfNotExists("carlos.ramirez@estudiante.edu", "alumno123", Usuario.Rol.ALUMNO, 3, "alumno Carlos Ramírez");
        
        log.info("Inicializacion completada");
    }

    private void createUserIfNotExists(String email, String password, Usuario.Rol rol, 
                                       Integer referenceId, String descripcion) {
        if (!usuarioRepository.existsByEmail(email)) {
            log.info("No se encontró usuario {}. Creando usuario...", descripcion);
            
            Usuario usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRol(rol);
            usuario.setReferenceId(referenceId);
            usuario.setActivo(true);
            
            usuarioRepository.save(usuario);
            
            log.info("=================================================");
            log.info("Usuario {} creado exitosamente:", descripcion);
            log.info("Email: {}", email);
            log.info("Password: {}", password);
            log.info("Rol: {}", rol);
            log.info("=================================================");
        } else {
            log.info("Usuario {} ya existe en la base de datos", email);
        }
    }
}
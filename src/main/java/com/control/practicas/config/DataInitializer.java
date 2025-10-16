package com.control.practicas.config;

import models.User;
import repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando verificacion de base de datos y datos iniciales...");

        if (!userRepository.existsByEmail("admin@controlpracticas.com")) {
            log.info("No se encontro usuario administrador. Creando usuario admin...");

            User admin = new User();
            admin.setEmail("admin@controlpracticas.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(User.Rol.ADMIN);
            admin.setActivo(true);
            userRepository.save(admin);
            log.info("=================================================");
            log.info("Usuario administrador creado exitosamente:");
            log.info("Email: admin@controlpracticas.com");
            log.info("Password: admin123");
            log.info("=================================================");
        	} else {
        		log.info("Usuario de prueba administrador ya existe en la base de datos");
        	}
        if (!userRepository.existsByEmail("alumno@controlpracticas.com")) {
            log.info("No se encontro usuario alumno. Creando usuario alumno...");   
            User alumno = new User();
            alumno.setEmail("alumno@controlpracticas.com");
            alumno.setPassword(passwordEncoder.encode("alumno123"));
            alumno.setRol(User.Rol.ALUMNO);
            alumno.setActivo(true);
            userRepository.save(alumno);
            log.info("=================================================");
            log.info("Usuario alumno creado exitosamente:");
            log.info("Email: alumno@controlpracticas.com");
            log.info("Password: alumno123");
            log.info("=================================================");
         } else {
        	log.info("Usuario de prueba alumno ya existe en la base de datos");
        	}
        if (!userRepository.existsByEmail("tutor@controlpracticas.com")) {
            log.info("No se encontro usuario tutor. Creando usuario tutor...");   
            User tutor = new User();
            tutor.setEmail("tutor@controlpracticas.com");
            tutor.setPassword(passwordEncoder.encode("tutor123"));
            tutor.setRol(User.Rol.TUTOR_CURSO);
            tutor.setActivo(true);
            userRepository.save(tutor);
            log.info("=================================================");
            log.info("Usuario de prueba tutor creado exitosamente:");
            log.info("Email: tutor@controlpracticas.com");
            log.info("Password: tutor123");
            log.info("=================================================");
         } else {
        	log.info("Usuariode prueba tutor ya existe en la base de datos");
        	}
        if (!userRepository.existsByEmail("practicas@controlpracticas.com")) {
            log.info("No se encontro usuario tutor de practicas. Creando usuario tutor de practicas...");   
            User practicas = new User();
            practicas.setEmail("practicas@controlpracticas.com");
            practicas.setPassword(passwordEncoder.encode("practicas123"));
            practicas.setRol(User.Rol.TUTOR_PRACTICAS);
            practicas.setActivo(true);
            userRepository.save(practicas);
            log.info("=================================================");
            log.info("Usuario tutor de practicas creado exitosamente:");
            log.info("Email: practicas@controlpracticas.com");
            log.info("Password: practicas123");
            log.info("=================================================");
         } else {
        	log.info("Usuario de prueba tutor ya existe en la base de datos");
        	}     
        log.info("Inicializacion completada");
    }
}
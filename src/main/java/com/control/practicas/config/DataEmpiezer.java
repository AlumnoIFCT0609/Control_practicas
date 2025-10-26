package com.control.practicas.config;


import models.*;
import repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
public class DataEmpiezer {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            EmpresaRepository empresaRepository,
            TutorCursoRepository tutorCursoRepository,
            CursoRepository cursoRepository,
            TutorPracticasRepository tutorPracticasRepository,
            AlumnoRepository alumnoRepository,
            CriterioEvaluacionRepository criterioRepository,
            CapacidadEvaluacionRepository capacidadRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            System.out.println("🔍 Verificando datos iniciales...");

            // ============================================
            // EMPRESAS
            // ============================================
            if (empresaRepository.count() == 0) {
                System.out.println("📦 Insertando empresas...");
                
                Empresa empresa1 = new Empresa();
                empresa1.setNombre("TechSolutions S.L.");
                empresa1.setCif("B12345678");
                empresa1.setDireccion("Calle Mayor 123, Sevilla");
                empresa1.setTelefono("954123456");
                empresa1.setEmail("info@techsolutions.com");
                empresa1.setPersonaContacto("María García");
                empresa1.setSector("Tecnología");
                empresa1.setActiva(true);
                empresaRepository.save(empresa1);

                Empresa empresa2 = new Empresa();
                empresa2.setNombre("Marketing Digital Pro");
                empresa2.setCif("B87654321");
                empresa2.setDireccion("Av. Constitución 45, Sevilla");
                empresa2.setTelefono("954987654");
                empresa2.setEmail("contacto@marketingpro.com");
                empresa2.setPersonaContacto("Carlos Ruiz");
                empresa2.setSector("Marketing");
                empresa2.setActiva(true);
                empresaRepository.save(empresa2);
                
                System.out.println("✅ Empresas insertadas: " + empresaRepository.count());
            }

            // ============================================
            // TUTORES DE CURSO
            // ============================================
            if (tutorCursoRepository.count() == 0) {
                System.out.println("👨‍🏫 Insertando tutores de curso...");
                
                TutorCurso tutor1 = new TutorCurso();
                tutor1.setNombre("Ana");
                tutor1.setApellidos("Martínez López");
                tutor1.setDni("12345678A");
                tutor1.setEmail("ana.martinez@instituto.edu");
                tutor1.setTelefono("600111222");
                tutor1.setEspecialidad("Desarrollo de Aplicaciones Web");
                tutor1.setActivo(true);
                tutorCursoRepository.save(tutor1);

                TutorCurso tutor2 = new TutorCurso();
                tutor2.setNombre("Pedro");
                tutor2.setApellidos("Sánchez Fernández");
                tutor2.setDni("87654321B");
                tutor2.setEmail("pedro.sanchez@instituto.edu");
                tutor2.setTelefono("600333444");
                tutor2.setEspecialidad("Administración de Sistemas");
                tutor2.setActivo(true);
                tutorCursoRepository.save(tutor2);
                
                System.out.println("✅ Tutores de curso insertados: " + tutorCursoRepository.count());
            }

            // ============================================
            // CURSOS
            // ============================================
            if (cursoRepository.count() == 0) {
                System.out.println("📚 Insertando cursos...");
                
                TutorCurso tutor1 = tutorCursoRepository.findById(1L).orElseThrow();
                TutorCurso tutor2 = tutorCursoRepository.findById(2L).orElseThrow();

                Curso curso1 = new Curso();
                curso1.setCodigo("DAW01");;
                curso1.setNombre("Desarrollo de Aplicaciones Web - DAW");
                curso1.setDescripcion("Curso de formación en desarrollo web full stack");
                curso1.setDuracion(2000);
                curso1.setFechaInicio(LocalDate.of(2024, 9, 1));
                curso1.setFechaFin(LocalDate.of(2026, 6, 30));
                curso1.setTutorCurso(tutor1);
                curso1.setActivo(true);
                cursoRepository.save(curso1);

                Curso curso2 = new Curso();
                curso2.setCodigo("ASIR");
                curso2.setNombre("Administración de Sistemas Informáticos - ASIR");
                curso2.setDescripcion("Curso de administración de sistemas y redes");
                curso2.setDuracion(2000);
                curso2.setFechaInicio(LocalDate.of(2024, 9, 1));
                curso2.setFechaFin(LocalDate.of(2026, 6, 30));
                curso2.setTutorCurso(tutor2);
                curso2.setActivo(true);
                cursoRepository.save(curso2);
                
                System.out.println("✅ Cursos insertados: " + cursoRepository.count());
            }

            // ============================================
            // TUTORES DE PRÁCTICAS
            // ============================================
            if (tutorPracticasRepository.count() == 0) {
                System.out.println("👔 Insertando tutores de prácticas...");
                
                Empresa empresa1 = empresaRepository.findById(1L).orElseThrow();
                Empresa empresa2 = empresaRepository.findById(2L).orElseThrow();

                TutorPracticas tutorPrac1 = new TutorPracticas();
                tutorPrac1.setNombre("Luis");
                tutorPrac1.setApellidos("González Pérez");
                tutorPrac1.setDni("11111111C");
                tutorPrac1.setEmail("luis.gonzalez@techsolutions.com");
                tutorPrac1.setTelefono("600555666");
                tutorPrac1.setEmpresa(empresa1);
                tutorPrac1.setCargo("Jefe de Desarrollo");
                tutorPrac1.setHorario("L-V 9:00-17:00");
                tutorPrac1.setActivo(true);
                tutorPracticasRepository.save(tutorPrac1);

                TutorPracticas tutorPrac2 = new TutorPracticas();
                tutorPrac2.setNombre("Laura");
                tutorPrac2.setApellidos("Rodríguez Díaz");
                tutorPrac2.setDni("22222222D");
                tutorPrac2.setEmail("laura.rodriguez@marketingpro.com");
                tutorPrac2.setTelefono("600777888");
                tutorPrac2.setEmpresa(empresa2);
                tutorPrac2.setCargo("Directora de Marketing");
                tutorPrac2.setHorario("L-V 8:30-16:30");
                tutorPrac2.setActivo(true);
                tutorPracticasRepository.save(tutorPrac2);
                
                System.out.println("✅ Tutores de prácticas insertados: " + tutorPracticasRepository.count());
            }

            // ============================================
            // ALUMNOS
            // ============================================
            if (alumnoRepository.count() == 0) {
                System.out.println("🎓 Insertando alumnos...");
                
                Curso curso1 = cursoRepository.findById(1L).orElseThrow();
                Curso curso2 = cursoRepository.findById(2L).orElseThrow();
                Empresa empresa1 = empresaRepository.findById(1L).orElseThrow();
                Empresa empresa2 = empresaRepository.findById(2L).orElseThrow();
                TutorPracticas tutorPrac1 = tutorPracticasRepository.findById(1L).orElseThrow();
                TutorPracticas tutorPrac2 = tutorPracticasRepository.findById(2L).orElseThrow();

                Alumno alumno1 = new Alumno();
                alumno1.setNombre("Juan");
                alumno1.setApellidos("Pérez García");
                alumno1.setDni("33333333E");
                alumno1.setFechaNacimiento(LocalDate.of(2002, 5, 15));
                alumno1.setEmail("juan.perez@estudiante.edu");
                alumno1.setTelefono("650111222");
                alumno1.setCurso(curso1);
                alumno1.setEmpresa(empresa1);
                alumno1.setTutorPracticas(tutorPrac1);
                alumno1.setDuracionPracticas(90);
                alumno1.setHorario("L-V 9:00-14:00");
                alumno1.setFechaInicio(LocalDate.of(2025, 3, 1));
                alumno1.setFechaFin(LocalDate.of(2025, 5, 30));
                alumno1.setActivo(true);
                alumno1.setFechaActualizacion(null);
                alumno1.setFechaCreacion(null);
                alumnoRepository.save(alumno1);

                Alumno alumno2 = new Alumno();
                alumno2.setNombre("María");
                alumno2.setApellidos("López Martínez");
                alumno2.setDni("44444444F");
                alumno2.setFechaNacimiento(LocalDate.of(2001, 8, 22));
                alumno2.setEmail("maria.lopez@estudiante.edu");
                alumno2.setTelefono("650333444");
                alumno2.setCurso(curso1);
                alumno2.setEmpresa(empresa2);
                alumno2.setTutorPracticas(tutorPrac2);
                alumno2.setDuracionPracticas(90);
                alumno2.setHorario("L-V 9:00-14:00");
                alumno2.setFechaInicio(LocalDate.of(2025, 3, 1));
                alumno2.setFechaFin(LocalDate.of(2025, 5, 30));
                alumno2.setActivo(true);
                alumno2.setFechaActualizacion(null);
                alumno2.setFechaCreacion(null);
                alumnoRepository.save(alumno2);

                Alumno alumno3 = new Alumno();
                alumno3.setNombre("Carlos");
                alumno3.setApellidos("Ramírez Soto");
                alumno3.setDni("55555555G");
                alumno3.setFechaNacimiento(LocalDate.of(2003, 2, 10));
                alumno3.setEmail("carlos.ramirez@estudiante.edu");
                alumno3.setTelefono("650555666");
                alumno3.setCurso(curso2);
                alumno3.setEmpresa(empresa1);
                alumno3.setTutorPracticas(tutorPrac1);
                alumno3.setDuracionPracticas(90);
                alumno3.setHorario("L-V 8:00-13:00");
                alumno3.setFechaInicio(LocalDate.of(2025, 3, 1));
                alumno3.setFechaFin(LocalDate.of(2025, 5, 30));
                alumno3.setActivo(true);
                alumno3.setFechaActualizacion(null);
                alumno3.setFechaCreacion(null);
                alumnoRepository.save(alumno3);
                
                System.out.println("✅ Alumnos insertados: " + alumnoRepository.count());
            }

            // ============================================
            // CRITERIOS DE EVALUACIÓN
            // ============================================
            if (criterioRepository.count() == 0) {
                System.out.println("📋 Insertando criterios de evaluación...");
                
                CriterioEvaluacion criterio1 = new CriterioEvaluacion();
                criterio1.setNombre("Competencias Técnicas");
                criterio1.setDescripcion("Habilidades técnicas y conocimientos aplicados");
                criterio1.setPeso(40.00f);
                criterio1.setActivo(true);
                criterioRepository.save(criterio1);

                CriterioEvaluacion criterio2 = new CriterioEvaluacion();
                criterio2.setNombre("Actitud y Comportamiento");
                criterio2.setDescripcion("Actitud profesional y comportamiento en el trabajo");
                criterio2.setPeso(30.00f);
                criterio2.setActivo(true);
                criterioRepository.save(criterio2);

                CriterioEvaluacion criterio3 = new CriterioEvaluacion();
                criterio3.setNombre("Trabajo en Equipo");
                criterio3.setDescripcion("Capacidad de colaboración y comunicación");
                criterio3.setPeso(30.00f);
                criterio3.setActivo(true);
                criterioRepository.save(criterio3);
                
                System.out.println("✅ Criterios de evaluación insertados: " + criterioRepository.count());
            }

            // ============================================
            // CAPACIDADES DE EVALUACIÓN
            // ============================================
            if (capacidadRepository.count() == 0) {
                System.out.println("⭐ Insertando capacidades de evaluación...");
                
                CriterioEvaluacion criterio1 = criterioRepository.findById(1L).orElseThrow();
                CriterioEvaluacion criterio2 = criterioRepository.findById(2L).orElseThrow();
                CriterioEvaluacion criterio3 = criterioRepository.findById(3L).orElseThrow();

                // Capacidades del Criterio 1
                CapacidadEvaluacion cap1 = new CapacidadEvaluacion();
                cap1.setCriterio(criterio1);
                cap1.setNombre("Conocimientos Técnicos");
                cap1.setDescripcion("Dominio de tecnologías y herramientas");
                cap1.setPuntuacionMaxima(10);
                cap1.setActivo(true);
                capacidadRepository.save(cap1);

                CapacidadEvaluacion cap2 = new CapacidadEvaluacion();
                cap2.setCriterio(criterio1);
                cap2.setNombre("Resolución de Problemas");
                cap2.setDescripcion("Capacidad para resolver problemas técnicos");
                cap2.setPuntuacionMaxima(10);
                cap2.setActivo(true);
                capacidadRepository.save(cap2);

                CapacidadEvaluacion cap3 = new CapacidadEvaluacion();
                cap3.setCriterio(criterio1);
                cap3.setNombre("Calidad del Trabajo");
                cap3.setDescripcion("Calidad y precisión en las tareas realizadas");
                cap3.setPuntuacionMaxima(10);
                cap3.setActivo(true);
                capacidadRepository.save(cap3);

                // Capacidades del Criterio 2
                CapacidadEvaluacion cap4 = new CapacidadEvaluacion();
                cap4.setCriterio(criterio2);
                cap4.setNombre("Puntualidad");
                cap4.setDescripcion("Cumplimiento de horarios establecidos");
                cap4.setPuntuacionMaxima(10);
                cap4.setActivo(true);
                capacidadRepository.save(cap4);

                CapacidadEvaluacion cap5 = new CapacidadEvaluacion();
                cap5.setCriterio(criterio2);
                cap5.setNombre("Iniciativa");
                cap5.setDescripcion("Proactividad y toma de iniciativa");
                cap5.setPuntuacionMaxima(10);
                cap5.setActivo(true);
                capacidadRepository.save(cap5);

                CapacidadEvaluacion cap6 = new CapacidadEvaluacion();
                cap6.setCriterio(criterio2);
                cap6.setNombre("Responsabilidad");
                cap6.setDescripcion("Compromiso y responsabilidad con las tareas");
                cap6.setPuntuacionMaxima(10);
                cap6.setActivo(true);
                capacidadRepository.save(cap6);

                // Capacidades del Criterio 3
                CapacidadEvaluacion cap7 = new CapacidadEvaluacion();
                cap7.setCriterio(criterio3);
                cap7.setNombre("Comunicación");
                cap7.setDescripcion("Habilidades de comunicación efectiva");
                cap7.setPuntuacionMaxima(10);
                cap7.setActivo(true);
                capacidadRepository.save(cap7);

                CapacidadEvaluacion cap8 = new CapacidadEvaluacion();
                cap8.setCriterio(criterio3);
                cap8.setNombre("Colaboración");
                cap8.setDescripcion("Capacidad de trabajar en equipo");
                cap8.setPuntuacionMaxima(10);
                cap8.setActivo(true);
                capacidadRepository.save(cap8);
                
                System.out.println("✅ Capacidades de evaluación insertadas: " + capacidadRepository.count());
            }

            // ============================================
            // USUARIOS
            // ============================================
        /*    if (userRepository.count() == 0) {
                System.out.println("👤 Insertando usuarios...");
                
                // Contraseña por defecto: "password123"
                String defaultPassword = passwordEncoder.encode("pass123");

                Usuario admin = new Usuario();
                admin.setEmail("admin@practicas.edu");
                admin.setPassword(defaultPassword);
                admin.setRol("ADMIN");
                admin.setReferenceId(1L);
                admin.setActivo(true);
                userRepository.save(admin);

                Usuario tutorCurso1 = new Usuario();
                tutorCurso1.setEmail("ana.martinez@instituto.edu");
                tutorCurso1.setPassword(defaultPassword);
                tutorCurso1.setRol("TUTOR_CURSO");
                tutorCurso1.setReferenceId(1L);
                tutorCurso1.setActivo(true);
                userRepository.save(tutorCurso1);

                Usuario tutorCurso2 = new Usuario();
                tutorCurso2.setEmail("pedro.sanchez@instituto.edu");
                tutorCurso2.setPassword(defaultPassword);
                tutorCurso2.setRol("TUTOR_CURSO");
                tutorCurso2.setReferenceId(2L);
                tutorCurso2.setActivo(true);
                userRepository.save(tutorCurso2);

                Usuario tutorPrac1 = new Usuario();
                tutorPrac1.setEmail("luis.gonzalez@techsolutions.com");
                tutorPrac1.setPassword(defaultPassword);
                tutorPrac1.setRol("TUTOR_PRACTICAS");
                tutorPrac1.setReferenceId(1L);
                tutorPrac1.setActivo(true);
                userRepository.save(tutorPrac1);

                Usuario tutorPrac2 = new Usuario();
                tutorPrac2.setEmail("laura.rodriguez@marketingpro.com");
                tutorPrac2.setPassword(defaultPassword);
                tutorPrac2.setRol("TUTOR_PRACTICAS");
                tutorPrac2.setReferenceId(2L);
                tutorPrac2.setActivo(true);
                userRepository.save(tutorPrac2);

                Usuario alumno1 = new Usuario();
                alumno1.setEmail("juan.perez@estudiante.edu");
                alumno1.setPassword(defaultPassword);
                alumno1.setRol("ALUMNO");
                alumno1.setReferenceId(1L);
                alumno1.setActivo(true);
                userRepository.save(alumno1);

                Usuario alumno2 = new Usuario();
                alumno2.setEmail("maria.lopez@estudiante.edu");
                alumno2.setPassword(defaultPassword);
                alumno2.setRol("ALUMNO");
                alumno2.setReferenceId(2L);
                alumno2.setActivo(true);
                userRepository.save(alumno2);

                Usuario alumno3 = new Usuario();
                alumno3.setEmail("carlos.ramirez@estudiante.edu");
                alumno3.setPassword(defaultPassword);
                alumno3.setRol("ALUMNO");
                alumno3.setReferenceId(3L);
                alumno3.setActivo(true);
                userRepository.save(alumno3);
                
                System.out.println("✅ Usuarios insertados: " + userRepository.count());
                System.out.println("🔑 Contraseña por defecto para todos los usuarios: password123");
            } */

            System.out.println("\n✅ ¡Inicialización de datos completada!");
            System.out.println("================================================");
            System.out.println("📊 Resumen:");
            System.out.println("  - Empresas: " + empresaRepository.count());
            System.out.println("  - Tutores de Curso: " + tutorCursoRepository.count());
            System.out.println("  - Cursos: " + cursoRepository.count());
            System.out.println("  - Tutores de Prácticas: " + tutorPracticasRepository.count());
            System.out.println("  - Alumnos: " + alumnoRepository.count());
            System.out.println("  - Criterios de Evaluación: " + criterioRepository.count());
            System.out.println("  - Capacidades de Evaluación: " + capacidadRepository.count());
            System.out.println("  - Usuarios: " + usuarioRepository.count());
            System.out.println("================================================\n");
        };
    }
}

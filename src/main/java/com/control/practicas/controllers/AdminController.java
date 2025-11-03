package com.control.practicas.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.hibernate.engine.internal.Collections;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.control.practicas.models.Alumno;
import com.control.practicas.models.Curso;
import com.control.practicas.models.Empresa;
import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.services.AlumnoService;
import com.control.practicas.services.CursoService;
import com.control.practicas.services.EmpresaService;
import com.control.practicas.services.TutorCursoService;
import com.control.practicas.services.TutorPracticasService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final CursoService cursoService;
    private final EmpresaService empresaService;
    private final TutorPracticasService tutorPracticasService;
    private final TutorCursoService tutorCursoService;
    private final AlumnoService alumnoService;
    public AdminController(CursoService cursoService, EmpresaService empresaService, 
    						TutorPracticasService tutorPracticasService,
    						TutorCursoService tutorCursoService,
    						AlumnoService alumnoService){
    	 this.cursoService = cursoService;
    	 this.empresaService= empresaService;
    	 this.tutorPracticasService=tutorPracticasService;
    	 this.alumnoService=alumnoService;
    	 this.tutorCursoService=tutorCursoService;

    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Curso> cursos = cursoService.listarTodos();
        List<Empresa> empresas = empresaService.listarTodas(); 
        List<TutorPracticas> tutorp = tutorPracticasService.listarTodos();
        List<Alumno> alumnos = alumnoService.listarTodos();
        // Crear un mapa con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }
        
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorp", tutorp);
        model.addAttribute("alumnos",alumnos);
  
        model.addAttribute("viewName", "admin/dashboard");
        return "layout";
    }
 
  //
    
    
    @GetMapping("/reportes/report")
    public String reports(Model model) {
        // Enlistan los cursos existentes
        List<Curso> cursos = cursoService.listarTodos();
        // Enlistan las empresas
        List<Empresa> empresas = empresaService.listarTodas();
        // Enlistan los tutores del curso
        List<TutorCurso> tutorC = tutorCursoService.listarTodos();
        // y los tutores de prácticas
        List<TutorPracticas> tutorP = tutorPracticasService.listarTodos();
        // y, los alumnos también
        List<Alumno> alumnos = alumnoService.listarTodos();

        // Creamos un mapeo con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }

        // Calcular estadísticas de cursos
        long cursosActivos = cursos.stream().filter(Curso::isActivo).count();
        long cursosFinalizados = cursos.stream().filter(c -> !c.isActivo()).count();
        // Calcular estadísticas de empresas
        long empresasActivas = empresas.stream().filter(Empresa::getActiva).count();
        long empresasInactivas = empresas.stream().filter(e -> !e.getActiva()).count();
        // Calculo estadísticas de alumnos
        long alumnosEnPracticas = alumnos.stream().filter(Alumno::isActivo).count();
        long alumnosFinalizados = alumnos.stream().filter(a -> !a.isActivo()).count();
        // Calculo estadisticas de Tutores
        long tutorCurso = tutorC.stream().filter(TutorCurso::getActivo).count();
        long tutorCursoInac = tutorC.stream().filter(tc -> !tc.getActivo()).count();
        long tutorPracticas = tutorP.stream().filter(TutorPracticas::getActivo).count();
        long tutorPracticasInac = tutorP.stream().filter(tp -> !tp.getActivo()).count();

        // ==================== SERIALIZACIÓN PARA JAVASCRIPT ====================
        // Convertir entidades a JSON simple para evitar problemas de serialización
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        try {
            // Serializar cursos con datos mínimos necesarios
            List<Map<String, Object>> cursosSimplificados = cursos.stream().map(c -> {
                Map<String, Object> cursoMap = new HashMap<>();
                cursoMap.put("id", c.getId());
                cursoMap.put("nombre", c.getNombre());
                cursoMap.put("activo", c.isActivo());
                cursoMap.put("duracion", c.getDuracion());
                
                // Fechas en formato ISO para JavaScript
                if (c.getFechaInicio() != null) {
                    cursoMap.put("fechaInicio", new int[]{
                        c.getFechaInicio().getYear(),
                        c.getFechaInicio().getMonthValue(),
                        c.getFechaInicio().getDayOfMonth()
                    });
                }
                if (c.getFechaFin() != null) {
                    cursoMap.put("fechaFin", new int[]{
                        c.getFechaFin().getYear(),
                        c.getFechaFin().getMonthValue(),
                        c.getFechaFin().getDayOfMonth()
                    });
                }
                
                // Tutor simplificado
                if (c.getTutorCurso() != null) {
                    Map<String, Object> tutorMap = new HashMap<>();
                    tutorMap.put("id", c.getTutorCurso().getId());
                    tutorMap.put("nombre", c.getTutorCurso().getNombre());
                    tutorMap.put("apellidos", c.getTutorCurso().getApellidos());
                    cursoMap.put("tutorCurso", tutorMap);
                }
                
                return cursoMap;
            }).collect(Collectors.toList());
            
            // Serializar empresas
            List<Map<String, Object>> empresasSimplificadas = empresas.stream().map(e -> {
                Map<String, Object> empresaMap = new HashMap<>();
                empresaMap.put("id", e.getId());
                empresaMap.put("nombre", e.getNombre());
                empresaMap.put("activo", e.getActiva());
                
                // Contar alumnos sin cargar la relación completa
                if (e.getAlumnos() != null) {
                    empresaMap.put("alumnos", new ArrayList<>(Collections.nCopies(e.getAlumnos().size(), null)));
                } else {
                    empresaMap.put("alumnos", new ArrayList<>());
                }
                
                return empresaMap;
            }).collect(Collectors.toList());
            
            // Serializar tutores de curso
            List<Map<String, Object>> tutoresCursoSimplificados = tutorC.stream().map(t -> {
                Map<String, Object> tutorMap = new HashMap<>();
                tutorMap.put("id", t.getId());
                tutorMap.put("nombre", t.getNombre());
                tutorMap.put("apellidos", t.getApellidos());
                tutorMap.put("activo", t.getActivo());
                
                // Contar cursos sin cargar la relación completa
                if (t.getCursos() != null) {
                    tutorMap.put("cursos", new ArrayList<>(Collections.nCopies(t.getCursos().size(), null)));
                } else {
                    tutorMap.put("cursos", new ArrayList<>());
                }
                
                return tutorMap;
            }).collect(Collectors.toList());
            
            // Serializar tutores de prácticas
            List<Map<String, Object>> tutoresPracticasSimplificados = tutorP.stream().map(t -> {
                Map<String, Object> tutorMap = new HashMap<>();
                tutorMap.put("id", t.getId());
                tutorMap.put("nombre", t.getNombre());
                tutorMap.put("apellidos", t.getApellidos());
                tutorMap.put("activo", t.getActivo());
                
                // Empresa simplificada
                if (t.getEmpresa() != null) {
                    Map<String, Object> empresaMap = new HashMap<>();
                    empresaMap.put("id", t.getEmpresa().getId());
                    empresaMap.put("nombre", t.getEmpresa().getNombre());
                    tutorMap.put("empresa", empresaMap);
                }
                
                // Contar alumnos sin cargar la relación completa
                if (t.getAlumnos() != null) {
                    tutorMap.put("alumnos", new ArrayList<>(Collections.nCopies(t.getAlumnos().size(), null)));
                } else {
                    tutorMap.put("alumnos", new ArrayList<>());
                }
                
                return tutorMap;
            }).collect(Collectors.toList());
            
            // Convertir a JSON strings
            String cursosJson = mapper.writeValueAsString(cursosSimplificados);
            String empresasJson = mapper.writeValueAsString(empresasSimplificadas);
            String tutoresCursoJson = mapper.writeValueAsString(tutoresCursoSimplificados);
            String tutoresPracticasJson = mapper.writeValueAsString(tutoresPracticasSimplificados);
            String alumnosPorCursoJson = mapper.writeValueAsString(alumnosPorCurso);
            
            // Añadir JSON strings al modelo
            model.addAttribute("cursosJson", cursosJson);
            model.addAttribute("empresasJson", empresasJson);
            model.addAttribute("tutoresCursoJson", tutoresCursoJson);
            model.addAttribute("tutoresPracticasJson", tutoresPracticasJson);
            model.addAttribute("alumnosPorCursoJson", alumnosPorCursoJson);
            
            System.out.println("========================");
            System.out.println("JSON generado correctamente");
            System.out.println("Cursos: " + cursosSimplificados.size());
            System.out.println("========================");
            
        } catch (Exception e) {
            System.err.println("Error al serializar datos: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Campos del modelo (para las tablas HTML)
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorP", tutorP);
        model.addAttribute("tutorC", tutorC);
        model.addAttribute("alumnos", alumnos);

        // Campos calculados
        model.addAttribute("tutorCurso", tutorCurso);
        model.addAttribute("tutorCursoInac", tutorCursoInac);
        model.addAttribute("tutorPracticas", tutorPracticas);
        model.addAttribute("tutorPracticasInac", tutorPracticasInac);
        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("cursosFinalizados", cursosFinalizados);
        model.addAttribute("empresasActivas", empresasActivas);
        model.addAttribute("empresasInactivas", empresasInactivas);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);

        // Cargamos la vista y enviamos la plantilla
        model.addAttribute("viewName", "admin/reportes/report");
        return "layout";
    }
    
    
    //
    
    
    
    
    
    
 /*   
    
    @GetMapping("/reportes/report")
    public String reports(Model model) {
    	//  se enlistan los cursos existentes cursos 
    	List<Curso> cursos = cursoService.listarTodos();
    	//  se enlistan las empresas
        List<Empresa> empresas = empresaService.listarTodas();
        //  se enlistan los tutores del curso
        List<TutorCurso> tutorC = tutorCursoService.listarTodos();
        //  y los tutores de prácticas
        List<TutorPracticas> tutorP = tutorPracticasService.listarTodos();    
        // y, los alumnos también
        List<Alumno> alumnos = alumnoService.listarTodos();

        // Creamos un mapeo con el número de alumnos por curso
        Map<Long, Long> alumnosPorCurso = new HashMap<>();
        for (Curso curso : cursos) {
            long numeroAlumnos = cursoService.contarAlumnosPorCurso(curso.getId());
            alumnosPorCurso.put(curso.getId(), numeroAlumnos);
        }

        // Calcular estadísticas de cursos
        long cursosActivos = cursos.stream().filter(Curso::isActivo).count();
        long cursosFinalizados = cursos.stream().filter(c -> !c.isActivo()).count();
        // Calcular estadísticas de empresas
        long empresasActivas = empresas.stream().filter(Empresa::getActiva).count();
        long empresasInactivas = empresas.stream().filter(e -> !e.getActiva()).count();
        // Calculo estadísticas de alumnos
        long alumnosEnPracticas = alumnos.stream().filter(Alumno::isActivo).count();
        long alumnosFinalizados = alumnos.stream().filter(a -> !a.isActivo()).count();
        //Calculo estadisticas de Tutores
        long tutorCurso = tutorC.stream().filter(TutorCurso::getActivo).count();
        long tutorCursoInac = tutorC.stream().filter(tc ->!tc.getActivo()).count();
        long tutorPracticas = tutorP.stream().filter(TutorPracticas::getActivo).count();
        long tutorPracticasInac = tutorP.stream().filter(tp ->!tp.getActivo()).count();
        
        //testeo de paso de variables 
        System.out.println("========================");
        System.out.println("Todos los tutores de Curso: "+ tutorC.size());
        System.out.println("Todas las empresas de Curso: "+ empresas.size());
        System.out.println("========================");
        
        //Campos del modelo
        model.addAttribute("cursos", cursos);
        model.addAttribute("alumnosPorCurso", alumnosPorCurso);
        model.addAttribute("empresas", empresas);
        model.addAttribute("tutorP", tutorP);
        model.addAttribute("tutorC", tutorC);
        model.addAttribute("alumnos", alumnos);
        
        // Campos calculados
        model.addAttribute("tutorCurso",tutorCurso);
        model.addAttribute("tutorCursoInac",tutorCursoInac);
        model.addAttribute("tutorPracticas",tutorPracticas);
        model.addAttribute("tutorPracticasInac",tutorPracticasInac);
        model.addAttribute("cursosActivos", cursosActivos);
        model.addAttribute("cursosFinalizados", cursosFinalizados);
        model.addAttribute("empresasActivas", empresasActivas);
        model.addAttribute("empresasInactivas", empresasInactivas);
        model.addAttribute("alumnosEnPracticas", alumnosEnPracticas);
        model.addAttribute("alumnosFinalizados", alumnosFinalizados);

        
        //cargamos la vista y enviamos la plantilla
        model.addAttribute("viewName", "admin/reportes/report");
        return "layout";
    }   
    */
    /*
     *  @GetMapping("/reportes/report")
    public String reports(Model model) {
        List<Curso> cursos = cursoService.listarTodos();

        LocalDate fechaMin = cursos.stream()
                .map(Curso::getFechaInicio)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
        LocalDate fechaMax = cursos.stream()
                .map(Curso::getFechaFin)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
        long totalDias = ChronoUnit.DAYS.between(fechaMin, fechaMax);

        // Colores para tutores (puede ser un mapa fijo)
        Map<String, String> coloresTutor = new HashMap<>();
        String[] paleta = {"#007bff","#28a745","#ffc107","#dc3545","#6f42c1","#fd7e14"};
        int index = 0;

        List<Map<String,Object>> cursosConTimeline = new ArrayList<>();
        for(Curso c : cursos){
            Map<String,Object> map = new HashMap<>();
            map.put("curso", c);

            long diasDesdeInicio = ChronoUnit.DAYS.between(fechaMin, c.getFechaInicio());
            long duracion = ChronoUnit.DAYS.between(c.getFechaInicio(), c.getFechaFin());

            double inicioPct = (diasDesdeInicio*100.0)/totalDias;
            double duracionPct = (duracion*100.0)/totalDias;
            map.put("inicioPct", inicioPct);
            map.put("duracionPct", duracionPct);

            String tutorNombre = (c.getTutorCurso()!=null)?c.getTutorCurso().getNombre():"Sin asignar";
            if(!coloresTutor.containsKey(tutorNombre)){
                coloresTutor.put(tutorNombre, paleta[index%paleta.length]);
                index++;
            }
            map.put("color", coloresTutor.get(tutorNombre));

            cursosConTimeline.add(map);
        }
        
        model.addAttribute("cursosConTimeline", cursosConTimeline);
        model.addAttribute("coloresTutor", coloresTutor);
        model.addAttribute("viewName", "admin/reportes/report");

        return "layout";
    }
     * 
     * 
     * */
    
    
    
    
    
}
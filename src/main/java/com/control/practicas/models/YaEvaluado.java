package com.control.practicas.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="yaevaluado",
       uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "tutor_practicas_id", "empresa_id", "curso_id"}))
public class YaEvaluado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "tutor_practicas_id", nullable = false)
    private Long tutorPracticasId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAlumnoId() { return alumnoId; }
    public void setAlumnoId(Long alumnoId) { this.alumnoId = alumnoId; }

    public Long getTutorPracticasId() { return tutorPracticasId; }
    public void setTutorPracticasId(Long tutorPracticasId) { this.tutorPracticasId = tutorPracticasId; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}


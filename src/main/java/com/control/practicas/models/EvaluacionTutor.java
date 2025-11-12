	package com.control.practicas.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "evaluaciontutor")
public class EvaluacionTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "tutorPracticas",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_evalTutor_tutorPracticas")
    )
    private TutorPracticas tutorPracticas;

    @ManyToOne
    @JoinColumn(
        name = "tutorCurso",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_evalTutor_tutorCurso")
    )
    private TutorCurso tutorCurso;

    @Column(precision = 4, scale = 2)
    private BigDecimal puntuacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(columnDefinition = "TEXT")
    private String aspectosPositivos;

    @Column(columnDefinition = "TEXT")
    private String aspectosMejorar;

    @Column(nullable = false)
    private LocalDate fecha;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TutorPracticas getTutorPracticas() {
        return tutorPracticas;
    }

    public void setTutorPracticas(TutorPracticas tutorPracticas) {
        this.tutorPracticas = tutorPracticas;
    }

    public TutorCurso getTutorCurso() {
        return tutorCurso;
    }

    public void setTutorCurso(TutorCurso tutorCurso) {
        this.tutorCurso = tutorCurso;
    }

    public BigDecimal getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(BigDecimal puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getAspectosPositivos() {
        return aspectosPositivos;
    }

    public void setAspectosPositivos(String aspectosPositivos) {
        this.aspectosPositivos = aspectosPositivos;
    }

    public String getAspectosMejorar() {
        return aspectosMejorar;
    }

    public void setAspectosMejorar(String aspectosMejorar) {
        this.aspectosMejorar = aspectosMejorar;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    @Transient // ← importante, para que JPA no lo persista
    public Long getTutorCursoId() {
        return tutorCurso != null ? tutorCurso.getId() : null;
    }

    @Transient
    public void setTutorCursoId(Long id) {
        if (id != null) {
            this.tutorCurso = new TutorCurso();
            this.tutorCurso.setId(id);
        } else {
            this.tutorCurso = null;
        }
    }

 // Métodos @Transient para tutorPracticasId
    @Transient
    public Long getTutorPracticasId() {
        return tutorPracticas != null ? tutorPracticas.getId() : null;
    }

    @Transient
    public void setTutorPracticasId(Long id) {
        if (id != null) {
            this.tutorPracticas = new TutorPracticas();
            this.tutorPracticas.setId(id);
        } else {
            this.tutorPracticas = null;
        }
    }
    
    
    
}


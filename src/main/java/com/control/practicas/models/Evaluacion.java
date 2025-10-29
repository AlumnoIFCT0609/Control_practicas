package com.control.practicas.models;

import jakarta.persistence.*;
//import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacion")

public class Evaluacion {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alumno", referencedColumnName = "id")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "tutorPracticas", referencedColumnName = "id")
    private TutorPracticas tutorPracticas;

    @ManyToOne
    @JoinColumn(name = "capacidad", referencedColumnName = "id")
    private CapacidadEvaluacion capacidad;

    @Column(name = "puntuacion", precision = 4, scale = 2)
    private BigDecimal puntuacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "fechaCreacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public TutorPracticas getTutorPracticas() {
        return tutorPracticas;
    }

    public void setTutorPracticas(TutorPracticas tutorPracticas) {
        this.tutorPracticas = tutorPracticas;
    }

    public CapacidadEvaluacion getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(CapacidadEvaluacion capacidad) {
        this.capacidad = capacidad;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}


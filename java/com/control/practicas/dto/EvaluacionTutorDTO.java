package com.control.practicas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EvaluacionTutorDTO {

    private Long id;
    private Long tutorCursoId;
    private Long tutorPracticasId;

    private BigDecimal puntuacion;
    private String observaciones;
    private String aspectosPositivos;
    private String aspectosMejorar;

    private LocalDate fecha;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTutorCursoId() {
        return tutorCursoId;
    }

    public void setTutorCursoId(Long tutorCursoId) {
        this.tutorCursoId = tutorCursoId;
    }

    public Long getTutorPracticasId() {
        return tutorPracticasId;
    }

    public void setTutorPracticasId(Long tutorPracticasId) {
        this.tutorPracticasId = tutorPracticasId;
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
}



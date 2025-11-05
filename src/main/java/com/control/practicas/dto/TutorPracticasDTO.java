package com.control.practicas.dto;

public class TutorPracticasDTO {
    private Long id;
    private String nombre;
    
    public TutorPracticasDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
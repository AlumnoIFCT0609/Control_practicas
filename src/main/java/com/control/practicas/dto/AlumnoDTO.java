package com.control.practicas.dto;

public class AlumnoDTO {
    private Long id;
    private String nombre;
    
    public AlumnoDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
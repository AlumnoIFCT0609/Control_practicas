package com.control.practicas.dto;

public class AlumnoDTO {
    private Long id;
    private String nombre;
    private String apellidos;
    
    public AlumnoDTO(Long id, String nombre, String apellidos) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos=apellidos;
    }
    public AlumnoDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
}
package com.control.practicas.dto;

public class CursoDTO {
	private Long id;
    private String nombre;
    
    public CursoDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}

package com.control.practicas.models;

import jakarta.persistence.*;

import java.time.LocalDate;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Empresa {
    

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(unique = true, nullable = false, length = 20)
    private String cif;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(length = 15)
    private String telefono;
    
    @Column(length = 100, unique = true)
    private String email;
    
    @Column(length = 100)
    private String sector;
    
    @Column(length = 100,name = "personacontacto")
    private String personaContacto;
    
    @Column(name = "fechacreacion", updatable = false)
    private LocalDate fechaCreacion;

    
    
    
    @PrePersist
    public void onCreate() {  // es protected lo pongo public para el test
        this.fechaCreacion = LocalDate.now();
    }
    
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<TutorPracticas> tutoresPracticas = new ArrayList<>();
    
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Alumno> alumnos = new ArrayList<>();
    
    @Column(nullable = false)
    private Boolean activa = true;
    

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}



	public String getPersonaContacto() {
		return personaContacto;
	}

	public void setPersonaContacto(String personaContacto) {
		this.personaContacto = personaContacto;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public List<TutorPracticas> getTutoresPracticas() {
		return tutoresPracticas;
	}

	public void setTutoresPracticas(List<TutorPracticas> tutoresPracticas) {
		this.tutoresPracticas = tutoresPracticas;
	}

	public List<Alumno> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Alumno> alumnos) {
		this.alumnos = alumnos;
	}

	public Boolean getActiva() {
		return activa;
	}

	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
    
    
}

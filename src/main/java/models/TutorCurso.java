package models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tutorcurso")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class TutorCurso {
     
   

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellidos;
    
    @Column(unique = true, length = 20, nullable=false)
    private String dni;
    
    @Column(length = 15)
    private String telefono;
    
    @Column(length = 100)
    private String especialidad;
    
    @Column(name = "fechaactualizacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaActualizacion;
    
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    @Column(length = 100, unique = true)
    private String email;
    
    @Column(nullable = false)
    private Boolean activo = true;
   
    
    public LocalDateTime getUltimoAcceso() {
		return fechaActualizacion;
	}
    
	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	@OneToMany(mappedBy = "tutorCurso", cascade = CascadeType.ALL)
    private List<Curso> cursos = new ArrayList<>();

	public void setActivo(boolean b) {
		this.activo=b;
		
	}

	public boolean getActivo() {

		return activo;
	}
}

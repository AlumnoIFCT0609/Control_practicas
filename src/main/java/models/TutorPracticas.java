package models;

import jakarta.persistence.*;

//import java.time.LocalDate;
import java.time.LocalDateTime;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tutorpracticas")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class TutorPracticas {
    
    

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String apellidos;
    
    @Column(nullable = false, length = 9)
    private String dni;
    
    @Column(nullable = true, length = 100)
    private String email;
    
    @Column(length = 15)
    private String telefono;
    
    @Column(length = 100)
    private String cargo;
    
    @ManyToOne
    @JoinColumn(
        name = "empresa",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_tutorPracticas_empresa")
    )
    private Empresa empresa;
    
    @Column(length = 200)
    private String horario;
    
    @OneToMany(mappedBy = "tutorPracticas", cascade = CascadeType.ALL)
    private List<Alumno> alumnos = new ArrayList<>();
    
    
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fechaactualizacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "fechacreacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

	public Empresa getEmpresa() {	
		return empresa;
	}
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public List<Alumno> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Alumno> alumnos) {
		this.alumnos = alumnos;
	}

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public void setEmpresa(Empresa empresa) {
		//this.empresa = empresa;
	}
}

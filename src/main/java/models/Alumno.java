package models;

import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

//import java.util.ArrayList;
//import java.util.List;

import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "alumno", uniqueConstraints = {
    @UniqueConstraint(columnNames = "dni"),
    @UniqueConstraint(columnNames = "email")
})
public class Alumno {

    public String getTelefono() {
		return telefono;
	}


	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public Integer getDuracionPracticas() {
		return duracionPracticas;
	}


	public void setDuracionPracticas(Integer duracionPracticas) {
		this.duracionPracticas = duracionPracticas;
	}


	public String getHorario() {
		return horario;
	}


	public void setHorario(String horario) {
		this.horario = horario;
	}


	public LocalDate getFechaInicio() {
		return fechaInicio;
	}


	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}


	public LocalDate getFechaFin() {
		return fechaFin;
	}


	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}


	public Boolean getActivo() {
		return activo;
	}


	public void setActivo(Boolean activo) {
		this.activo = activo;
	}


	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}


	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}


	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}


	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellidos;
    
    @Column(nullable = false, length = 20, unique = true)
    private String dni;
    
    @Column(name = "fechanacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 100, unique = true)
    private String email;
    
    @Column(nullable = true, length = 15)
    private String telefono;
 
    @ManyToOne
    @JoinColumn(
        name = "curso",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_alumno_curso")
    )
    private Curso curso;

    @ManyToOne
    @JoinColumn(
        name = "empresa",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_alumno_empresa")
    )
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(
        name = "tutorpracticas",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_alumno_tutorPracticas")
    )
    private TutorPracticas tutorPracticas;

    @Column(name = "duracionpracticas")
    private Integer duracionPracticas;
    
    @Column(nullable = false, length = 200)
    private String horario;

    @Column(name = "fechainicio", nullable = false)
    @CreationTimestamp
    private LocalDate fechaInicio;
    
    @Column(name = "fechafin", nullable = false)
    @CreationTimestamp
    private LocalDate fechaFin;
    
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
        this.fechaActualizacion = LocalDateTime.now();
        }
        
    
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public TutorPracticas getTutorPracticas() {
        return tutorPracticas;
    }

    public void setTutorPracticas(TutorPracticas tutorPracticas) {
        this.tutorPracticas = tutorPracticas;
    }


}

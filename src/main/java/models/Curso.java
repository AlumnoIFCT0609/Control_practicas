package models;

import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curso")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(nullable = true, length = 100)
    private String codigo;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "fechainicio")
    private LocalDate fechaInicio;
    
    @Column(name = "fechafin")
    private LocalDate fechaFin;
    
    @Column(name = "duracion")
    private Integer duracion;
    
    @Column(name = "fechacreacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        
    }
    
    @ManyToOne
    @JoinColumn(
        name = "tutorcurso",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_curso_tutor")
    )
    private TutorCurso tutorCurso;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Alumno> alumnos = new ArrayList<>();
    
    public Long getId() {
		return id;
	}


	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public Integer getDuracion() {
		return duracion;
	}

	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public TutorCurso getTutorCurso() {
		return tutorCurso;
	}

	public void setTutorCurso(TutorCurso tutorCurso) {
		this.tutorCurso = tutorCurso;
	}

	public List<Alumno> getAlumnos() {
		return alumnos;
	}

	public void setAlumnos(List<Alumno> alumnos) {
		this.alumnos = alumnos;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	@Column(nullable = false)
    private Boolean activo = true;

	public void setCodigo(String codigo) {
		this.codigo=codigo;
	}

	public void setNombre(String nombre) {
		this.nombre=nombre;
	}
	public void setHorasPracticas(int duracion) {
		this.duracion=duracion;
	}


	public void setId(Long id) {
		// TODO Auto-generated method stub
		//no va a hacer nada
	}
	
	
	
}

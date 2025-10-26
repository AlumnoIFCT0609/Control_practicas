package models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "observaciondiaria")
public class ObservacionDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "alumno",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_observacion_alumno")
    )
    private Alumno alumno;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(columnDefinition = "TEXT")
    private String actividades;

    @Column(columnDefinition = "TEXT")
    private String explicaciones;

    @Column(columnDefinition = "TEXT")
    private String observacionesAlumno;

    @Column(columnDefinition = "TEXT")
    private String observacionesTutor;

    @Column
    private Integer horasRealizadas;

    @Column(name = "fechaCreacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    @Transient
    private Long alumnoId;

    // Y su getter y setter:
    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }
    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getActividades() {
        return actividades;
    }

    public void setActividades(String actividades) {
        this.actividades = actividades;
    }

    public String getExplicaciones() {
        return explicaciones;
    }

    public void setExplicaciones(String explicaciones) {
        this.explicaciones = explicaciones;
    }

    public String getObservacionesAlumno() {
        return observacionesAlumno;
    }

    public void setObservacionesAlumno(String observacionesAlumno) {
        this.observacionesAlumno = observacionesAlumno;
    }

    public String getObservacionesTutor() {
        return observacionesTutor;
    }

    public void setObservacionesTutor(String observacionesTutor) {
        this.observacionesTutor = observacionesTutor;
    }

    public Integer getHorasRealizadas() {
        return horasRealizadas;
    }

    public void setHorasRealizadas(Integer horasRealizadas) {
        this.horasRealizadas = horasRealizadas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }



	
}

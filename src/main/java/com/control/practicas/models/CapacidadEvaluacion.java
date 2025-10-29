package com.control.practicas.models;

import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
//import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "capacidadevaluacion")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class CapacidadEvaluacion {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	 @ManyToOne
	    @JoinColumn(
	        name = "criterio",
	        referencedColumnName = "id",
	        foreignKey = @ForeignKey(name = "fk_capacidad_criterio")
	    )
	    private CriterioEvaluacion criterio;

	    @Column(name = "nombre", length = 100, nullable = false)
	    private String nombre;

	    @Column(name = "descripcion", columnDefinition = "TEXT")
	    private String descripcion;

	    @Column(name = "puntuacionmaxima")
	    private Integer puntuacionMaxima = 10;

	    @Column(name = "activo")
	    private Boolean activo = true;
	    
	    @CreationTimestamp
	    @Column(name = "fechacreacion", updatable = false)
	    private LocalDateTime fechaCreacion;
	    
	    @PrePersist
	    protected void onCreate() {
	        this.fechaCreacion = LocalDateTime.now();
	        } 
	    
	    // Getters y setters

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public CriterioEvaluacion getCriterio() {
	        return criterio;
	    }

	    public void setCriterio(CriterioEvaluacion criterio) {
	        this.criterio = criterio;
	    }

	    public String getNombre() {
	        return nombre;
	    }

	    public void setNombre(String nombre) {
	        this.nombre = nombre;
	    }

	    public String getDescripcion() {
	        return descripcion;
	    }

	    public void setDescripcion(String descripcion) {
	        this.descripcion = descripcion;
	    }

	    public Integer getPuntuacionMaxima() {
	        return puntuacionMaxima;
	    }

	    public void setPuntuacionMaxima(Integer puntuacionMaxima) {
	        this.puntuacionMaxima = puntuacionMaxima;
	    }

	    public Boolean getActivo() {
	        return activo;
	    }

	    public void setActivo(Boolean activo) {
	        this.activo = activo;
	    }
 
}



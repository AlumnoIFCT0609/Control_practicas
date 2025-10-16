package models;


import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class User {
    
   
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "ultimo_acceso", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime ultimoAcceso;
    
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    
    // Enum para los roles
    public enum Rol {
        ADMIN,
        TUTOR_CURSO,
        TUTOR_PRACTICAS,
        ALUMNO
    }
    
    // Constructor personalizado
    public User(String email, String password, Rol rol) {
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }
    public User() {}
    
    public void setEmail(String email) {
    	this.email=email;
    }
    public String getEmail() {
    	return email;
    }
    
    public void setActivo(boolean activo) {
    	this.activo=activo;
    }
    
    public boolean getActivo() {
    	return activo;
    }
    
    public void setRol(Rol rol) {
    	this.rol=rol;
    }
    public Rol getRol() {
    	return rol;
    }
    
    public void setPassword(String password) {
    	this.password=password;
    }
    
    public String getPassword() {
    	return password;
    }
    
    public Long getReferenceId() {
    	return referenceId;
    }
    
    public void setReferenceId(long referenceId) {
    	this.referenceId=referenceId;
    }
    public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}
    
    // Método para actualizar último acceso
    public void setUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }
	public  LocalDateTime getUltimoAcceso() {
		return ultimoAcceso;
	}
	public void setFechaCreacion() {
		this.fechaCreacion= LocalDateTime.now();
		
	}
}

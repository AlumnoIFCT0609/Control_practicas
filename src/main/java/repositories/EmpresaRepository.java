package repositories;

import models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    /**
     * Buscar empresa por CIF (único)
     */
    Optional<Empresa> findByCif(String cif);
    
    /**
     * Buscar empresa por email (único)
     */
    Optional<Empresa> findByEmail(String email);
    
    /**
     * Listar solo empresas activas
     */
    List<Empresa> findByActivaTrue();
    
    /**
     * Listar empresas inactivas
     */
    List<Empresa> findByActivaFalse();
    
    /**
     * Contar empresas activas
     */
    long countByActivaTrue();
    
    /**
     * Buscar empresas por sector
     */
    List<Empresa> findBySector(String sector);
    
    /**
     * Buscar empresas por nombre (contiene, sin distinción mayúsculas/minúsculas)
     */
    List<Empresa> findByNombreContainingIgnoreCase(String nombre);
}
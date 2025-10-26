package repositories;


import models.Usuario;
import models.Usuario.Rol;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Usuario.Rol rol);
    List<Usuario> findByActivoTrue();
    Optional<Usuario> findByRolAndReferenceId(Rol rol, Long referenceId);
    
    boolean existsByEmail(String email);
}
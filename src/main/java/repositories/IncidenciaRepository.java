package repositories;

import models.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    List<Incidencia> findByAlumnoId(Long alumnoId);
    List<Incidencia> findByEstado(Incidencia.Estado estado);
    List<Incidencia> findByAlumnoIdOrderByFechaDesc(Long alumnoId);
}



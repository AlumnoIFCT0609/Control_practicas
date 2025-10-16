package repositories;

import models.ObservacionDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObservacionDiariaRepository extends JpaRepository<ObservacionDiaria, Long> {
    List<ObservacionDiaria> findByAlumnoId(Long alumnoId);
    List<ObservacionDiaria> findByAlumnoIdOrderByFechaDesc(Long alumnoId);
    List<ObservacionDiaria> findByFecha(LocalDate fecha);
}


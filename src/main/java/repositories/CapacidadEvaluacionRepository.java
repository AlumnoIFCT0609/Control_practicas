package repositories;

import models.CapacidadEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacidadEvaluacionRepository extends JpaRepository<CapacidadEvaluacion, Long> {
    // Puedes agregar métodos personalizados si lo necesitas
}

package repositories;

import models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TutorPracticasRepository extends JpaRepository<TutorPracticas, Long> {
    List<TutorPracticas> findByEmpresaId(Long empresaId);
}

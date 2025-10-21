package repositories;

import models.TutorCurso;
import models.TutorPracticas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TutorPracticasRepository extends JpaRepository<TutorPracticas, Long> {
    List<TutorPracticas> findByEmpresaId(Long empresaId);
	 Optional<TutorPracticas> findByEmail(String email);
	 Optional<TutorPracticas> findByDni(String dni);
	 
}

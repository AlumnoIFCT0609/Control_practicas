package repositories;

import models.TutorCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TutorCursoRepository extends JpaRepository<TutorCurso, Long> {
    Optional<TutorCurso> findByDni(String dni);
    Optional<TutorCurso> findByEmail(String email);
}

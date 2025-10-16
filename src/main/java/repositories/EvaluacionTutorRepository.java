package repositories;

import models.EvaluacionTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionTutorRepository extends JpaRepository<EvaluacionTutor, Long> {
}

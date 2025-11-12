package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.control.practicas.models.YaEvaluado;

@Repository
public interface YaEvaluadoRepository extends JpaRepository<YaEvaluado, Long> {

    boolean existsByAlumnoIdAndTutorPracticasIdAndEmpresaIdAndCursoId(
        Long alumnoId, Long tutorPracticasId, Long empresaId, Long cursoId
    );
}



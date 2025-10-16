package repositories;

import models.Alumno;
import models.Curso;
import models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByEmpresa(Empresa empresa);
    List<Alumno> findByTutorPracticasId(Long tutorPracticasId);
    Optional<Alumno> findByDni(String dni);
    List<Alumno> findByCursoId(Long cursoId);
}


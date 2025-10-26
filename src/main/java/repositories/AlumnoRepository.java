package repositories;

import models.Alumno;
import models.Curso;
import models.Empresa;
import models.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    Optional<Alumno> findById(Long id);
    List<Alumno> findByCurso(Curso curso);
    List<Alumno> findByEmpresa(Empresa empresa);
    List<Alumno> findByTutorPracticas_Id(Long tutorPracticasId);
    List<Alumno> findByCurso_Id(Long cursoId);
    List<Alumno> findByEmpresa_Id(Long empresaId);
    Optional<Alumno> findByDni(String dni);
	Optional<Alumno> findByEmail(String email);

}
package com.control.practicas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import com.control.practicas.models.TutorCurso;
import com.control.practicas.models.TutorPracticas;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorPracticasRepository extends JpaRepository<TutorPracticas, Long> {
    List<TutorPracticas> findByEmpresaId(Long empresaId);
	 Optional<TutorPracticas> findByEmail(String email);
	 Optional<TutorPracticas> findByDni(String dni);
	 
}

package com.control.practicas.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.control.practicas.models.Curso;
import com.control.practicas.repositories.CursoRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CursoService {
    
    private final CursoRepository cursoRepository;
 // 🔹 Inyección de dependencias por constructor
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }
    
    @Transactional
    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }
    
    public Optional<Curso> buscarPorId(Long id) {
        return cursoRepository.findById(id);
    }
    
    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }
    
    public List<Curso> listarActivos() {
        return cursoRepository.findByActivoTrue();
    }
    
    public List<Curso> listarPorTutorCurso(Long tutorCursoId) {
        return cursoRepository.findByTutorCurso_Id(tutorCursoId);
    }
    
    @Transactional
    public void eliminar(Long id) {
        cursoRepository.deleteById(id);
    }
    
    public boolean existePorId(Long id) {
        return cursoRepository.existsById(id);
    }
    
    // Nuevo método para obtener el número de alumnos de un curso
    public long contarAlumnosPorCurso(Long cursoId) {
        return cursoRepository.contarAlumnosPorCurso(cursoId);
    }
}

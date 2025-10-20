package services;

import models.TutorPracticas;

import repositories.TutorPracticasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TutorPracticasService {
 
 private final TutorPracticasRepository tutorPracticasRepository;
 
 public TutorPracticasService(TutorPracticasRepository tutorPracticasRepository) {
     this.tutorPracticasRepository = tutorPracticasRepository;
 }
 
 
 public TutorPracticas guardar(TutorPracticas tutorPracticas) {
     return tutorPracticasRepository.save(tutorPracticas);
 }
 
 public Optional<TutorPracticas> buscarPorId(Long id) {
     return tutorPracticasRepository.findById(id);
 }
 
 public List<TutorPracticas> listarTodos() {
     return tutorPracticasRepository.findAll();
 }
 
 public List<TutorPracticas> listarPorEmpresa(Long empresaId) {
     return tutorPracticasRepository.findByEmpresaId(empresaId);
 }
 
 @Transactional
 public void eliminar(Long id) {
     tutorPracticasRepository.deleteById(id);
 }
}

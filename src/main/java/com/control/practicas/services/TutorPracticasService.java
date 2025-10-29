package com.control.practicas.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.TutorPracticas;
import com.control.practicas.models.Usuario;
import com.control.practicas.models.Usuario.Rol;
import com.control.practicas.repositories.TutorPracticasRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TutorPracticasService {
 
 private final TutorPracticasRepository tutorPracticasRepository;
 private final UsuarioService usuarioService;
 
 public TutorPracticasService(TutorPracticasRepository tutorPracticasRepository,
		 						UsuarioService usuarioService) {
     this.tutorPracticasRepository = tutorPracticasRepository;
     this.usuarioService = usuarioService;
 }
 
 
 public TutorPracticas guardar(TutorPracticas tutorPracticas) {
     return tutorPracticasRepository.save(tutorPracticas);
 }
 
 public Optional<TutorPracticas> buscarPorId(Long id) {
     return tutorPracticasRepository.findById(id);
 }
 public Optional<TutorPracticas> buscarPorDni(String dni) {
     return tutorPracticasRepository.findByDni(dni);
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
 public Usuario crearUsuarioParaTutorPracticas(Long tutorId) {
	    TutorPracticas tutor = tutorPracticasRepository.findById(tutorId)
	        .orElseThrow(() -> new IllegalArgumentException("Tutor de prácticas no encontrado"));
	    
	    return usuarioService.crearUsuarioParaEntidad(
	        tutor.getEmail(),
	        tutor.getDni(),
	        Rol.TUTOR_PRACTICAS,
	        tutor.getId(),
	        tutor.getActivo()
	    );
	}
}

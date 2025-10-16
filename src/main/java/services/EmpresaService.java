package services;

import models.Empresa;
import repositories.EmpresaRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class EmpresaService {
    
    private final EmpresaRepository empresaRepository=null;
    
    @Transactional
    public Empresa guardar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
    
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }
    
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }
    
    public List<Empresa> listarActivas() {
        return empresaRepository.findByActivaTrue();
    }
    
    @Transactional
    public void eliminar(Long id) {
        empresaRepository.deleteById(id);
    }

	public boolean existePorId(Long id) {
		return empresaRepository.existsById(id);
	}
}

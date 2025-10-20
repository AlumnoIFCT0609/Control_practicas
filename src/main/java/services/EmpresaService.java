package services;

import models.Empresa;
import repositories.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpresaService {
    
    private final EmpresaRepository empresaRepository;
    
    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }
    
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }
    
    public List<Empresa> listarActivas() {
        return empresaRepository.findByActivaTrue();
    }
    
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }
    
    public Optional<Empresa> buscarPorCif(String cif) {
        return empresaRepository.findByCif(cif);
    }
    
    public Optional<Empresa> buscarPorEmail(String email) {
        return empresaRepository.findByEmail(email);
    }
    
    public Empresa guardar(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
    
    public void eliminar(Long id) {
        empresaRepository.deleteById(id);
    }
    
    public void activar(Long id) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            empresa.setActiva(true);
            empresaRepository.save(empresa);
        }
    }
    
    public void desactivar(Long id) {
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            empresa.setActiva(false);
            empresaRepository.save(empresa);
        }
    }
    
    public boolean existePorCif(String cif) {
        return empresaRepository.findByCif(cif).isPresent();
    }
    
    public boolean existePorEmail(String email) {
        return empresaRepository.findByEmail(email).isPresent();
    }
    
    public long contarTodas() {
        return empresaRepository.count();
    }
    
    public long contarActivas() {
        return empresaRepository.countByActivaTrue();
    }
    
    public List<Empresa> buscarPorSector(String sector) {
        return empresaRepository.findBySector(sector);
    }
    
    public List<Empresa> buscarPorNombre(String nombre) {
        return empresaRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
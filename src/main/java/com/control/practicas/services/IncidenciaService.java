package com.control.practicas.services;

import org.springframework.beans.factory.annotation.Autowired;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import com.control.practicas.models.Incidencia;
import com.control.practicas.repositories.IncidenciaRepository;

import java.util.List;
import java.util.Optional;


@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    public List<Incidencia> listarTodas() {
        return incidenciaRepository.findAll();
    }

    public Optional<Incidencia> buscarPorId(Long id) {
        return incidenciaRepository.findById(id);
    }

    public Incidencia guardar(Incidencia incidencia) {
        return incidenciaRepository.save(incidencia);
    }

    public void eliminar(Long id) {
        incidenciaRepository.deleteById(id);
    }

    public boolean existePorId(Long id) {
        return incidenciaRepository.existsById(id);
    }
}

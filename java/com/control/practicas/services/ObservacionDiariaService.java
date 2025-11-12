package com.control.practicas.services;

import org.springframework.stereotype.Service;
import com.control.practicas.models.ObservacionDiaria;
import com.control.practicas.models.TutorPracticas;
import com.control.practicas.repositories.ObservacionDiariaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ObservacionDiariaService {
    
    private final ObservacionDiariaRepository observacionDiariaRepository;
    
    // 🔹 Inyección de dependencias por constructor
    public ObservacionDiariaService(ObservacionDiariaRepository observacionDiariaRepository) {
        this.observacionDiariaRepository = observacionDiariaRepository;
    }
    
    public List<ObservacionDiaria> listarTodas() {
        return observacionDiariaRepository.findAll();
    }
    
    public Optional<ObservacionDiaria> buscarPorId(Long id) {
        return observacionDiariaRepository.findById(id);
    }
    
    public ObservacionDiaria guardar(ObservacionDiaria observacion) {
        return observacionDiariaRepository.save(observacion);
    }
    
    public void eliminar(Long id) {
        observacionDiariaRepository.deleteById(id);
    }
    
    public boolean existePorId(Long id) {
        return observacionDiariaRepository.existsById(id);
    }
    
    public List<ObservacionDiaria> listarPorAlumno(Long alumnoId) {
        return observacionDiariaRepository.findByAlumno_Id(alumnoId);
    }
    
    public List<ObservacionDiaria> listarPorAlumnoOrdenadas(Long alumnoId) {
        return observacionDiariaRepository.findByAlumno_IdOrderByFechaDesc(alumnoId);
    }
    
    public List<ObservacionDiaria> listarPorFecha(LocalDate fecha) {
        return observacionDiariaRepository.findByFecha(fecha);
    }
    
    public long contarObservaciones(TutorPracticas tutor) {
        if (tutor == null) return 0;
        return observacionDiariaRepository.countByTutorPracticas(tutor);
    }
}

    
    


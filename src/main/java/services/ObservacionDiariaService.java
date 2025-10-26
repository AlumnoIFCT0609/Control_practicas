package services;

import models.ObservacionDiaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.ObservacionDiariaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ObservacionDiariaService {

    @Autowired
    private ObservacionDiariaRepository observacionDiariaRepository;

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
}

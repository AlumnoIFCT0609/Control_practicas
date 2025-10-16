package controllers;


import models.ObservacionDiaria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ObservacionDiariaService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/observaciones-diarias")
public class ObservacionDiariaController {

    @Autowired
    private ObservacionDiariaService observacionDiariaService;

    @GetMapping
    public List<ObservacionDiaria> listarTodas() {
        return observacionDiariaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObservacionDiaria> obtenerPorId(@PathVariable Long id) {
        return observacionDiariaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/alumno/{alumnoId}")
    public List<ObservacionDiaria> listarPorAlumno(@PathVariable Long alumnoId) {
        return observacionDiariaService.listarPorAlumno(alumnoId);
    }

    @GetMapping("/alumno/{alumnoId}/ordenadas")
    public List<ObservacionDiaria> listarPorAlumnoOrdenadas(@PathVariable Long alumnoId) {
        return observacionDiariaService.listarPorAlumnoOrdenadas(alumnoId);
    }

    @GetMapping("/fecha/{fecha}")
    public List<ObservacionDiaria> listarPorFecha(@PathVariable LocalDate fecha) {
        return observacionDiariaService.listarPorFecha(fecha);
    }

    @PostMapping
    public ObservacionDiaria crear(@RequestBody ObservacionDiaria observacion) {
        return observacionDiariaService.guardar(observacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObservacionDiaria> actualizar(@PathVariable Long id, @RequestBody ObservacionDiaria observacion) {
        return observacionDiariaService.buscarPorId(id)
                .map(o -> {
                    observacion.setId(id);
                    return ResponseEntity.ok(observacionDiariaService.guardar(observacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (observacionDiariaService.existePorId(id)) {
            observacionDiariaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

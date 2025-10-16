package controllers;

import models.CapacidadEvaluacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.CapacidadEvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/capacidades")
public class CapacidadEvaluacionController {

    @Autowired
    private CapacidadEvaluacionService capacidadEvaluacionService;

    @GetMapping
    public List<CapacidadEvaluacion> listarTodas() {
        return capacidadEvaluacionService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CapacidadEvaluacion> obtenerPorId(@PathVariable Long id) {
        return capacidadEvaluacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CapacidadEvaluacion crear(@RequestBody CapacidadEvaluacion capacidad) {
        return capacidadEvaluacionService.guardar(capacidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CapacidadEvaluacion> actualizar(@PathVariable Long id, @RequestBody CapacidadEvaluacion capacidad) {
        return capacidadEvaluacionService.buscarPorId(id)
                .map(c -> {
                    capacidad.setId(id);
                    return ResponseEntity.ok(capacidadEvaluacionService.guardar(capacidad));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (capacidadEvaluacionService.existePorId(id)) {
            capacidadEvaluacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

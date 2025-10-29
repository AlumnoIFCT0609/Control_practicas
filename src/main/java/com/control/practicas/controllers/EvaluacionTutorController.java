package com.control.practicas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.control.practicas.models.EvaluacionTutor;
import com.control.practicas.services.EvaluacionTutorService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluaciones-tutor")
public class EvaluacionTutorController {

    @Autowired
    private EvaluacionTutorService evaluacionTutorService;

    @GetMapping
    public List<EvaluacionTutor> listarTodas() {
        return evaluacionTutorService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluacionTutor> obtenerPorId(@PathVariable Long id) {
        return evaluacionTutorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public EvaluacionTutor crear(@RequestBody EvaluacionTutor evaluacion) {
        return evaluacionTutorService.guardar(evaluacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluacionTutor> actualizar(@PathVariable Long id, @RequestBody EvaluacionTutor evaluacion) {
        return evaluacionTutorService.buscarPorId(id)
                .map(e -> {
                    evaluacion.setId(id);
                    return ResponseEntity.ok(evaluacionTutorService.guardar(evaluacion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (evaluacionTutorService.existePorId(id)) {
            evaluacionTutorService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

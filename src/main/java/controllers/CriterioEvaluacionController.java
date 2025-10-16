package controllers;

import models.CriterioEvaluacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.CriterioEvaluacionService;

import java.util.List;

@RestController
@RequestMapping("/api/criterios")
public class CriterioEvaluacionController {

    @Autowired
    private CriterioEvaluacionService criterioEvaluacionService;

    @GetMapping
    public List<CriterioEvaluacion> listarTodos() {
        return criterioEvaluacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterioEvaluacion> obtenerPorId(@PathVariable Long id) {
        return criterioEvaluacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CriterioEvaluacion crear(@RequestBody CriterioEvaluacion criterio) {
        return criterioEvaluacionService.guardar(criterio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CriterioEvaluacion> actualizar(@PathVariable Long id, @RequestBody CriterioEvaluacion criterio) {
        return criterioEvaluacionService.buscarPorId(id)
                .map(c -> {
                 //   criterio.setId(id);
                    return ResponseEntity.ok(criterioEvaluacionService.guardar(criterio));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (criterioEvaluacionService.existePorId(id)) {
            criterioEvaluacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

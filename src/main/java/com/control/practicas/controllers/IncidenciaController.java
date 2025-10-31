package com.control.practicas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.control.practicas.models.Incidencia;
import com.control.practicas.services.IncidenciaService;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    // Inyección de dependencias por constructor
    public IncidenciaController(IncidenciaService incidenciaService) {
        this.incidenciaService = incidenciaService;
    }

    @GetMapping
    public List<Incidencia> listarTodas() {
        return incidenciaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incidencia> obtenerPorId(@PathVariable Long id) {
        return incidenciaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Incidencia crear(@RequestBody Incidencia incidencia) {
        return incidenciaService.guardar(incidencia);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incidencia> actualizar(@PathVariable Long id, @RequestBody Incidencia incidencia) {
        return incidenciaService.buscarPorId(id)
                .map(i -> {
                    incidencia.setId(id);
                    return ResponseEntity.ok(incidenciaService.guardar(incidencia));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (incidenciaService.existePorId(id)) {
            incidenciaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


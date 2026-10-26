package com.terminal.aviones_service.controller;

import com.terminal.aviones_service.dto.AvionAltaDTO;
import com.terminal.aviones_service.dto.AvionConPersonalDTO;
import com.terminal.aviones_service.service.AvionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/aviones")
public class AvionController {

    @Autowired
    private AvionService avionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR', 'PILOTO', 'AZAFATA')")
    public List<AvionConPersonalDTO> listarTodos() {
        log.info("Petición GET recibida en /aviones");
        return avionService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR', 'PILOTO', 'AZAFATA')")
    public ResponseEntity<AvionConPersonalDTO> obtenerPorId(@PathVariable Integer id) {
        log.info("Petición GET recibida en /aviones/{}", id);
        AvionConPersonalDTO avion = avionService.obtenerPorId(id);
        if (avion == null) {
            log.warn("Avión con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(avion);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<AvionConPersonalDTO> crear(@RequestBody AvionAltaDTO altaDTO) {
        log.info("Petición POST recibida para crear avión modelo: {}", altaDTO.modelo());
        AvionConPersonalDTO creado = avionService.guardar(altaDTO);
        log.info("Avión creado con éxito. ID asignado: {}", creado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<AvionConPersonalDTO> actualizar(@PathVariable Integer id, @RequestBody AvionAltaDTO altaDTO) {
        log.info("Petición PUT recibida para actualizar avión ID: {}", id);
        AvionConPersonalDTO actualizado = avionService.actualizar(id, altaDTO);
        if (actualizado == null) {
            log.warn("No se pudo actualizar, avión ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Avión ID {} actualizado con éxito", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        log.info("Petición DELETE recibida para eliminar avión ID: {}", id);
        try {
            if (avionService.eliminar(id)) {
                log.info("Avión ID {} eliminado exitosamente", id);
                return ResponseEntity.noContent().build();
            }
            log.warn("No se pudo eliminar, avión ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        } catch (ResponseStatusException e) {
            log.warn("Validación fallida al eliminar avión ID {}: {}", id, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
        }
    }
}
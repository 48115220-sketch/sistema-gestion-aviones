package com.terminal.vuelos_service.controller;

import com.terminal.vuelos_service.dto.VueloAltaDTO;
import com.terminal.vuelos_service.dto.VueloConAvionDTO;
import com.terminal.vuelos_service.service.VueloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/vuelos")
public class VueloController {

    @Autowired
    private VueloService vueloService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR', 'PILOTO', 'AZAFATA')")
    public List<VueloConAvionDTO> listarTodos() {
        log.info("Petición GET recibida en /vuelos");
        return vueloService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR', 'PILOTO', 'AZAFATA')")
    public ResponseEntity<VueloConAvionDTO> obtenerPorId(@PathVariable Integer id) {
        log.info("Petición GET recibida en /vuelos/{}", id);
        VueloConAvionDTO vuelo = vueloService.obtenerPorId(id);
        if (vuelo != null) {
            return ResponseEntity.ok(vuelo);
        }
        log.warn("Vuelo con ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/avion/{avionId}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR', 'PILOTO', 'AZAFATA')")
    public ResponseEntity<List<VueloConAvionDTO>> obtenerPorAvion(@PathVariable Integer avionId) {
        log.info("Petición GET recibida en /vuelos/avion/{}", avionId);
        return ResponseEntity.ok(vueloService.obtenerPorAvion(avionId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<VueloConAvionDTO> crear(@RequestBody VueloAltaDTO altaDTO) {
        log.info("Petición POST recibida para crear número de vuelo: {}", altaDTO.numeroVuelo());
        VueloConAvionDTO creado = vueloService.guardar(altaDTO);
        log.info("Vuelo registrado con éxito. ID asignado: {}", creado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<VueloConAvionDTO> actualizar(@PathVariable Integer id, @RequestBody VueloAltaDTO altaDTO) {
        log.info("Petición PUT recibida para actualizar vuelo ID: {}", id);
        VueloConAvionDTO actualizado = vueloService.actualizar(id, altaDTO);
        if (actualizado != null) {
            log.info("Vuelo ID {} actualizado exitosamente", id);
            return ResponseEntity.ok(actualizado);
        }
        log.warn("No se pudo actualizar, vuelo ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("Petición DELETE recibida para cancelar vuelo ID: {}", id);
        if (vueloService.eliminar(id)) {
            log.info("Vuelo ID {} cancelado con éxito", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("No se pudo cancelar, vuelo ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
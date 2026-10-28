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

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/aviones")
public class AvionController {

    @Autowired
    private AvionService avionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'PILOTO', 'AZAFATA', 'ADMINISTRATIVO')")
    public List<AvionConPersonalDTO> listarTodos() {
        log.info("Peticion GET recibida en /aviones");
        return avionService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PILOTO', 'AZAFATA', 'ADMINISTRATIVO')")
    public ResponseEntity<AvionConPersonalDTO> obtenerPorId(@PathVariable Integer id) {
        log.info("Peticion GET recibida en /aviones/{}", id);
        return ResponseEntity.ok(avionService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<AvionConPersonalDTO> crear(@RequestBody AvionAltaDTO altaDTO) {
        log.info("Peticion POST recibida para crear avion modelo: {}", altaDTO.modelo());
        AvionConPersonalDTO creado = avionService.guardar(altaDTO);
        log.info("Avion creado con exito. ID asignado: {}", creado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<AvionConPersonalDTO> actualizar(@PathVariable Integer id, @RequestBody AvionAltaDTO altaDTO) {
        log.info("Peticion PUT recibida para actualizar avion ID: {}", id);
        AvionConPersonalDTO actualizado = avionService.actualizar(id, altaDTO);
        log.info("Avion ID {} actualizado con exito", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("Peticion DELETE recibida para eliminar avion ID: {}", id);
        avionService.eliminar(id);
        log.info("Avion ID {} eliminado exitosamente", id);
        return ResponseEntity.noContent().build();
    }
}
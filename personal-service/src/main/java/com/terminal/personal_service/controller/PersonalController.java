package com.terminal.personal_service.controller;

import com.terminal.personal_service.dto.PersonalAltaDTO;
import com.terminal.personal_service.dto.PersonalDTO;
import com.terminal.personal_service.service.PersonalService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/admin/personal")
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'PILOTO', 'AZAFATA', 'ADMINISTRATIVO')")
    public List<PersonalDTO> listarTodos() {
        log.info("Peticion GET recibida en /admin/personal");
        return personalService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PILOTO', 'AZAFATA', 'ADMINISTRATIVO')")
    public ResponseEntity<PersonalDTO> obtenerPorId(@PathVariable Integer id) {
        log.info("Peticion GET recibida en /admin/personal/{}", id);
        PersonalDTO personal = personalService.obtenerPorId(id);
        if (personal == null) {
            log.warn("Personal de vuelo con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(personal);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<PersonalDTO> crear(@RequestBody PersonalAltaDTO altaDTO) {
        log.info("Peticion POST recibida para crear personal DNI: {}", altaDTO.dni());
        PersonalDTO creado = personalService.guardar(altaDTO);
        log.info("Personal de vuelo creado exitosamente con ID: {}", creado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<PersonalDTO> actualizar(@PathVariable Integer id, @RequestBody PersonalAltaDTO altaDTO) {
        log.info("Peticion PUT recibida para actualizar personal ID: {}", id);
        PersonalDTO actualizado = personalService.actualizar(id, altaDTO);
        if (actualizado == null) {
            log.warn("No se pudo actualizar, personal con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Personal ID {} actualizado exitosamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("Peticion DELETE recibida para eliminar personal ID: {}", id);
        if (personalService.eliminar(id)) {
            log.info("Personal ID {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        }
        log.warn("No se pudo eliminar, personal con ID {} no encontrado", id);
        return ResponseEntity.notFound().build();
    }
}
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
        return ResponseEntity.ok(personalService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<PersonalDTO> crear(@RequestBody PersonalAltaDTO altaDTO) {
        log.info("Peticion POST recibida para crear personal DNI: {}", altaDTO.dni());
        PersonalDTO creado = personalService.guardar(altaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<PersonalDTO> actualizar(@PathVariable Integer id, @RequestBody PersonalAltaDTO altaDTO) {
        log.info("Peticion PUT recibida para actualizar personal ID: {}", id);
        PersonalDTO actualizado = personalService.actualizar(id, altaDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        log.info("Peticion DELETE recibida para eliminar personal ID: {}", id);
        personalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
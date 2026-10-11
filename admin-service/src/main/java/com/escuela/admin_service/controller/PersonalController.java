package com.escuela.admin_service.controller;

import com.escuela.admin_service.dto.PersonalAltaDTO;
import com.escuela.admin_service.dto.PersonalDTO;
import com.escuela.admin_service.service.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/personal")
public class PersonalController {

    @Autowired
    private PersonalService docenteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DIRECTOR')")
    public List<PersonalDTO> listarTodos() {
        return docenteService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DIRECTOR')")
    public ResponseEntity<PersonalDTO> obtenerPorId(@PathVariable Integer id) {
        PersonalDTO docente = docenteService.obtenerPorId(id);
        if (docente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(docente);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO')")
    public ResponseEntity<PersonalDTO> crear(@RequestBody PersonalAltaDTO altaDTO) {
        PersonalDTO creado = docenteService.guardar(altaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO')")
    public ResponseEntity<PersonalDTO> actualizar(@PathVariable Integer id, @RequestBody PersonalAltaDTO altaDTO) {
        PersonalDTO actualizado = docenteService.actualizar(id, altaDTO);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (docenteService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

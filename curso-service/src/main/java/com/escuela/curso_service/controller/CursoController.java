package com.escuela.curso_service.controller;

import com.escuela.curso_service.dto.CursoAltaDTO;
import com.escuela.curso_service.dto.CursoConDocenteDTO;
import com.escuela.curso_service.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR')")
    public List<CursoConDocenteDTO> listarTodos() {
        return cursoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR')")
    public ResponseEntity<CursoConDocenteDTO> obtenerPorId(@PathVariable Integer id) {
        CursoConDocenteDTO curso = cursoService.obtenerPorId(id);
        if (curso == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(curso);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<CursoConDocenteDTO> crear(@RequestBody CursoAltaDTO altaDTO) {
        CursoConDocenteDTO creado = cursoService.guardar(altaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<CursoConDocenteDTO> actualizar(@PathVariable Integer id, @RequestBody CursoAltaDTO altaDTO) {
        CursoConDocenteDTO actualizado = cursoService.actualizar(id, altaDTO);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (cursoService.eliminar(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
        }
    }
}

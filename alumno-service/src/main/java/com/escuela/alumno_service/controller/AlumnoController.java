package com.escuela.alumno_service.controller;

import com.escuela.alumno_service.dto.AlumnoAltaDTO;
import com.escuela.alumno_service.dto.AlumnoConCursoDTO;
import com.escuela.alumno_service.service.AlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

    @Autowired
    private AlumnoService alumnoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR')")
    public List<AlumnoConCursoDTO> listarTodos() {
        return alumnoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR')")
    public ResponseEntity<AlumnoConCursoDTO> obtenerPorId(@PathVariable Integer id) {
        AlumnoConCursoDTO alumno = alumnoService.obtenerPorId(id);
        if (alumno != null) {
            return ResponseEntity.ok(alumno);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasAnyRole('ROOT', 'ADMINISTRATIVO', 'DOCENTE', 'PRECEPTOR', 'DIRECTOR')")
    public ResponseEntity<List<AlumnoConCursoDTO>> obtenerPorCurso(@PathVariable Integer cursoId) {
        return ResponseEntity.ok(alumnoService.obtenerPorCurso(cursoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<AlumnoConCursoDTO> crear(@RequestBody AlumnoAltaDTO altaDTO) {
        AlumnoConCursoDTO creado = alumnoService.guardar(altaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<AlumnoConCursoDTO> actualizar(@PathVariable Integer id, @RequestBody AlumnoAltaDTO altaDTO) {
        AlumnoConCursoDTO actualizado = alumnoService.actualizar(id, altaDTO);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRECEPTOR', 'ADMINISTRATIVO')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (alumnoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

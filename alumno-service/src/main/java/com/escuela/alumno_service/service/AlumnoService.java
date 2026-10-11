package com.escuela.alumno_service.service;

import com.escuela.alumno_service.dto.AlumnoAltaDTO;
import com.escuela.alumno_service.dto.AlumnoConCursoDTO;
import java.util.List;

public interface AlumnoService {
    List<AlumnoConCursoDTO> obtenerTodos();
    AlumnoConCursoDTO obtenerPorId(Integer id);
    AlumnoConCursoDTO guardar(AlumnoAltaDTO altaDTO);
    AlumnoConCursoDTO actualizar(Integer id, AlumnoAltaDTO altaDTO);
    boolean eliminar(Integer id);
    List<AlumnoConCursoDTO> obtenerPorCurso(Integer cursoId);
}

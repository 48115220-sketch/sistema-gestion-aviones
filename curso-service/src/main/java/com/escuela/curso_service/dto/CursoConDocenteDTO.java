package com.escuela.curso_service.dto;

public record CursoConDocenteDTO(
    Integer id,
    String nombre,
    Integer docenteId,
    Integer cupo,
    DocenteDTO docente
) {}
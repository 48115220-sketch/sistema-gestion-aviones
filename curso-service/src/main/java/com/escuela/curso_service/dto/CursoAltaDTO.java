package com.escuela.curso_service.dto;

public record CursoAltaDTO(
    String nombre,
    Integer docenteId,
    Integer cupo
) {}
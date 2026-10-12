package com.escuela.alumno_service.dto;

public record AlumnoAltaDTO(
    String nombre,
    String apellido,
    String dni,
    Integer cursoId,
    String estado
) {}
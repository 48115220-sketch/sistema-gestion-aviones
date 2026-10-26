package com.terminal.aviones_service.dto;

public record PersonalDTO(
    Integer id,
    String nombre,
    String apellido,
    String dni,
    Float sueldo,
    String cargo,
    String tipo
) {}
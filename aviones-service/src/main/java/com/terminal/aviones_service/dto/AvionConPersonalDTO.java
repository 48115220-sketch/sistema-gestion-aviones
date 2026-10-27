package com.terminal.aviones_service.dto;

public record AvionConPersonalDTO(
    Integer id,
    String modelo,
    Integer capacidadPersonas,
    Float capacidadEquipaje,
    Integer personalId,
    String estado,
    PersonalDTO personal
) {}
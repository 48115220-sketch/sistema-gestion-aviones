package com.terminal.vuelos_service.dto;

public record AvionDTO(
    Integer id,
    String modelo,
    Integer capacidadPersonas,
    Float capacidadEquipaje,
    Integer personalId
) {}
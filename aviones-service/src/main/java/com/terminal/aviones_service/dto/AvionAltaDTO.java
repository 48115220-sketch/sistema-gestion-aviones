package com.terminal.aviones_service.dto;

public record AvionAltaDTO(
    String modelo,
    Integer capacidadPersonas,
    Float capacidadEquipaje,
    Integer personalId,
    String estado
) {}
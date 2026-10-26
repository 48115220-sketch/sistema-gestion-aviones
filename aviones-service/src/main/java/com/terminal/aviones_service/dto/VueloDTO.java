package com.terminal.aviones_service.dto;

public record VueloDTO(
    Long id,
    String numeroVuelo,
    String origen,
    String destino,
    String horaSalida,
    String horaLlegada,
    Integer avionId,
    String estado
) {}
package com.terminal.vuelos_service.dto;

public record VueloDTO(
    Integer id,
    String numeroVuelo,
    String origen,
    String destino,
    String horaSalida,
    String horaLlegada,
    Integer avionId,
    String estado
) {}
package com.terminal.personal_service.dto;

import com.terminal.personal_service.entidad.TipoPersonal;

public record PersonalAltaDTO(
    String nombre,
    String apellido,
    String dni,
    Float sueldo,
    String cargo,
    TipoPersonal tipo
) {}
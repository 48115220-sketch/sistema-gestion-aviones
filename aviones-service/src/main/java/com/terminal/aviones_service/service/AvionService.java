package com.terminal.aviones_service.service;

import com.terminal.aviones_service.dto.AvionAltaDTO;
import com.terminal.aviones_service.dto.AvionConPersonalDTO;

import java.util.List;

public interface AvionService {
    List<AvionConPersonalDTO> obtenerTodos();
    AvionConPersonalDTO obtenerPorId(Integer id);
    AvionConPersonalDTO guardar(AvionAltaDTO altaDTO);
    AvionConPersonalDTO actualizar(Integer id, AvionAltaDTO altaDTO);
    boolean eliminar(Integer id);
}
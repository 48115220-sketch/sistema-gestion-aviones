package com.terminal.vuelos_service.service;

import com.terminal.vuelos_service.dto.VueloAltaDTO;
import com.terminal.vuelos_service.dto.VueloConAvionDTO;
import java.util.List;

public interface VueloService {
    List<VueloConAvionDTO> obtenerTodos();
    VueloConAvionDTO obtenerPorId(Integer id);
    VueloConAvionDTO guardar(VueloAltaDTO altaDTO);
    VueloConAvionDTO actualizar(Integer id, VueloAltaDTO altaDTO);
    boolean eliminar(Integer id);
    List<VueloConAvionDTO> obtenerPorAvion(Integer avionId);
}
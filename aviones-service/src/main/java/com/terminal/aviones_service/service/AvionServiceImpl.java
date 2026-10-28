package com.terminal.aviones_service.service;

import com.terminal.aviones_service.client.PersonalClient;
import com.terminal.aviones_service.client.VueloClient;
import com.terminal.aviones_service.dto.AvionAltaDTO;
import com.terminal.aviones_service.dto.AvionConPersonalDTO;
import com.terminal.aviones_service.dto.PersonalDTO;
import com.terminal.aviones_service.dto.VueloDTO;
import com.terminal.aviones_service.entidad.Avion;
import com.terminal.aviones_service.exception.ResourceNotFoundException;
import com.terminal.aviones_service.repository.AvionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class AvionServiceImpl implements AvionService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired(required = false)
    private PersonalClient personalClient;

    @Autowired(required = false)
    private VueloClient vueloClient;

    @Override
    public List<AvionConPersonalDTO> obtenerTodos() {
        log.debug("Recuperando todos los aviones");
        return avionRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public AvionConPersonalDTO obtenerPorId(Integer id) {
        log.debug("Buscando avion por ID: {}", id);
        return avionRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new ResourceNotFoundException("Avion con ID " + id + " no encontrado."));
    }

    @Override
    public AvionConPersonalDTO guardar(AvionAltaDTO altaDTO) {
        log.info("Guardando nuevo avion modelo: {}", altaDTO.modelo());

        if (altaDTO.modelo() == null || altaDTO.modelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo del avion no puede estar vacio.");
        }
        if (altaDTO.capacidadPersonas() != null && altaDTO.capacidadPersonas() <= 0) {
            throw new IllegalArgumentException("La capacidad de personas debe ser mayor a cero.");
        }

        Avion avion = new Avion();
        avion.setModelo(altaDTO.modelo());
        avion.setCapacidadPersonas(altaDTO.capacidadPersonas());
        avion.setCapacidadEquipaje(altaDTO.capacidadEquipaje());
        avion.setPersonalId(altaDTO.personalId());
        avion.setEstado(altaDTO.estado() != null ? altaDTO.estado() : "EN_SERVICIO");

        Avion guardado = avionRepository.save(avion);
        return convertirADTO(guardado);
    }

    @Override
    public AvionConPersonalDTO actualizar(Integer id, AvionAltaDTO altaDTO) {
        log.info("Actualizando avion ID: {}", id);

        Avion avion = avionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avion con ID " + id + " no encontrado."));

        if (altaDTO.modelo() == null || altaDTO.modelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo del avion no puede estar vacio.");
        }

        avion.setModelo(altaDTO.modelo());
        avion.setCapacidadPersonas(altaDTO.capacidadPersonas());
        avion.setCapacidadEquipaje(altaDTO.capacidadEquipaje());
        avion.setPersonalId(altaDTO.personalId());
        if (altaDTO.estado() != null) {
            avion.setEstado(altaDTO.estado());
        }

        Avion actualizado = avionRepository.save(avion);
        return convertirADTO(actualizado);
    }

    @Override
    public boolean eliminar(Integer id) {
        log.info("Eliminando avion ID: {}", id);

        if (!avionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Avion con ID " + id + " no encontrado.");
        }

        avionRepository.deleteById(id);
        return true;
    }

    private AvionConPersonalDTO convertirADTO(Avion avion) {
        PersonalDTO personal = null;
        if (avion.getPersonalId() != null && personalClient != null) {
            try {
                personal = personalClient.obtenerPersonalPorId(null, avion.getPersonalId());
            } catch (Exception e) {
                log.warn("No se pudo obtener informacion del personal ID {} para el avion ID {}: {}", 
                        avion.getPersonalId(), avion.getId(), e.getMessage());
            }
        }

        return new AvionConPersonalDTO(
                avion.getId(),
                avion.getModelo(),
                avion.getCapacidadPersonas(),
                avion.getCapacidadEquipaje(),
                avion.getPersonalId(),
                avion.getEstado(),
                personal
        );
    }
}
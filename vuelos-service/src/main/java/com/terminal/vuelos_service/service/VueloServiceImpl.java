package com.terminal.vuelos_service.service;

import com.terminal.vuelos_service.config.RabbitMQConfig;
import com.terminal.vuelos_service.dto.VueloAltaDTO;
import com.terminal.vuelos_service.dto.VueloConAvionDTO;
import com.terminal.vuelos_service.entidad.Vuelo;
import com.terminal.vuelos_service.event.VueloEvent;
import com.terminal.vuelos_service.event.VueloEstadoEvent;
import com.terminal.vuelos_service.exception.ResourceNotFoundException;
import com.terminal.vuelos_service.repository.VueloRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class VueloServiceImpl implements VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Override 
    public List<VueloConAvionDTO> obtenerTodos() { 
        log.debug("Obteniendo todos los vuelos registrados");
        return vueloRepository.findAll().stream()
                .map(this::convertirAConAvionDTO)
                .collect(Collectors.toList()); 
    }
    
    @Override 
    public VueloConAvionDTO obtenerPorId(Integer id) { 
        log.debug("Buscando vuelo con ID: {}", id);
        return vueloRepository.findById(id)
                .map(this::convertirAConAvionDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo con ID " + id + " no encontrado.")); 
    }
    
    @Override 
    public List<VueloConAvionDTO> obtenerPorAvion(Integer avionId) {
        log.debug("Buscando vuelos programados para el avion ID: {}", avionId);
        return vueloRepository.findByAvionIdAndEstado(avionId, "PROGRAMADO").stream()
                .map(this::convertirAConAvionDTO)
                .collect(Collectors.toList());
    }

    @Override 
    public VueloConAvionDTO guardar(VueloAltaDTO altaDTO) {
        log.info("Guardando nuevo vuelo N°: {} | Origen: {} | Destino: {}", altaDTO.numeroVuelo(), altaDTO.origen(), altaDTO.destino());

        if (altaDTO.numeroVuelo() == null || altaDTO.numeroVuelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de vuelo es obligatorio.");
        }
        if (vueloRepository.existsByNumeroVuelo(altaDTO.numeroVuelo())) {
            throw new IllegalArgumentException("El numero de vuelo " + altaDTO.numeroVuelo() + " ya se encuentra registrado.");
        }

        Vuelo vuelo = new Vuelo(); 
        vuelo.setNumeroVuelo(altaDTO.numeroVuelo()); 
        vuelo.setOrigen(altaDTO.origen()); 
        vuelo.setDestino(altaDTO.destino()); 
        vuelo.setHoraSalida(altaDTO.horaSalida()); 
        vuelo.setHoraLlegada(altaDTO.horaLlegada()); 
        vuelo.setAvionId(altaDTO.avionId()); 
        vuelo.setEstado(altaDTO.estado() != null && !altaDTO.estado().trim().isEmpty() ? altaDTO.estado() : "PROGRAMADO");

        Vuelo guardado = vueloRepository.save(vuelo);

        if (rabbitTemplate != null && guardado.getAvionId() != null) {
            try {
                VueloEvent evento = new VueloEvent(guardado.getId().longValue(), guardado.getAvionId().longValue());
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ALTA, evento);
                log.info("Evento de alta de vuelo enviado a RabbitMQ para avion ID: {}", guardado.getAvionId());
            } catch (Exception e) {
                log.warn("No se pudo publicar evento RabbitMQ de alta vuelo: {}", e.getMessage());
            }
        }

        return convertirAConAvionDTO(guardado);
    }

    @Override
    public VueloConAvionDTO actualizar(Integer id, VueloAltaDTO altaDTO) {
        log.info("Actualizando vuelo con ID: {}", id);

        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo con ID " + id + " no encontrado."));

        if (altaDTO.numeroVuelo() == null || altaDTO.numeroVuelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El numero de vuelo es obligatorio.");
        }
        if (vueloRepository.existsByNumeroVueloAndIdNot(altaDTO.numeroVuelo(), id)) {
            throw new IllegalArgumentException("El numero de vuelo " + altaDTO.numeroVuelo() + " ya pertenece a otro vuelo.");
        }

        String estadoAnterior = vuelo.getEstado();

        vuelo.setNumeroVuelo(altaDTO.numeroVuelo());
        vuelo.setOrigen(altaDTO.origen());
        vuelo.setDestino(altaDTO.destino());
        vuelo.setHoraSalida(altaDTO.horaSalida());
        vuelo.setHoraLlegada(altaDTO.horaLlegada());
        vuelo.setAvionId(altaDTO.avionId());
        if (altaDTO.estado() != null) {
            vuelo.setEstado(altaDTO.estado());
        }

        Vuelo actualizado = vueloRepository.save(vuelo);

        if (rabbitTemplate != null && !estadoAnterior.equalsIgnoreCase(actualizado.getEstado())) {
            try {
                VueloEstadoEvent estadoEvent = new VueloEstadoEvent(actualizado.getId().longValue(), actualizado.getEstado());
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ESTADO, estadoEvent);
                log.info("Evento cambio estado de vuelo enviado a RabbitMQ: {}", actualizado.getEstado());
            } catch (Exception e) {
                log.warn("No se pudo publicar evento RabbitMQ de estado vuelo: {}", e.getMessage());
            }
        }

        return convertirAConAvionDTO(actualizado);
    }

    @Override
    public boolean eliminar(Integer id) {
        log.info("Cancelando/eliminando vuelo ID: {}", id);

        Vuelo vuelo = vueloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vuelo con ID " + id + " no encontrado."));

        vueloRepository.delete(vuelo);

        if (rabbitTemplate != null && vuelo.getAvionId() != null) {
            try {
                VueloEvent evento = new VueloEvent(vuelo.getId().longValue(), vuelo.getAvionId().longValue());
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_BAJA, evento);
                log.info("Evento de baja de vuelo enviado a RabbitMQ para avion ID: {}", vuelo.getAvionId());
            } catch (Exception e) {
                log.warn("No se pudo publicar evento RabbitMQ de baja vuelo: {}", e.getMessage());
            }
        }

        return true;
    }

    private VueloConAvionDTO convertirAConAvionDTO(Vuelo vuelo) {
        return new VueloConAvionDTO(
                vuelo.getId(),
                vuelo.getNumeroVuelo(),
                vuelo.getOrigen(),
                vuelo.getDestino(),
                vuelo.getHoraSalida(),
                vuelo.getHoraLlegada(),
                vuelo.getAvionId(),
                vuelo.getEstado(),
                null
        );
    }
}
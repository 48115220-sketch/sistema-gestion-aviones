package com.terminal.vuelos_service.service;

import com.terminal.vuelos_service.config.RabbitMQConfig;
import com.terminal.vuelos_service.dto.VueloAltaDTO;
import com.terminal.vuelos_service.dto.VueloConAvionDTO;
import com.terminal.vuelos_service.entidad.Vuelo;
import com.terminal.vuelos_service.event.VueloEvent;
import com.terminal.vuelos_service.event.VueloEstadoEvent;
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

    @Autowired private VueloRepository vueloRepository;
    @Autowired(required = false) private RabbitTemplate rabbitTemplate;

    @Override 
    public List<VueloConAvionDTO> obtenerTodos() { 
        log.debug("Obteniendo todos los vuelos registrados");
        return vueloRepository.findAll().stream().map(this::convertirAConAvionDTO).collect(Collectors.toList()); 
    }
    
    @Override 
    public VueloConAvionDTO obtenerPorId(Integer id) { 
        log.debug("Buscando vuelo con ID: {}", id);
        return vueloRepository.findById(id).map(this::convertirAConAvionDTO).orElse(null); 
    }
    
    @Override 
    public List<VueloConAvionDTO> obtenerPorAvion(Integer avionId) {
        log.debug("Buscando vuelos programados para el avion ID: {}", avionId);
        return vueloRepository.findByAvionIdAndEstado(avionId, "PROGRAMADO").stream().map(this::convertirAConAvionDTO).collect(Collectors.toList());
    }

    @Override 
    public VueloConAvionDTO guardar(VueloAltaDTO altaDTO) {
        log.info("Guardando nuevo vuelo N°: {} | Origen: {} | Destino: {}", altaDTO.numeroVuelo(), altaDTO.origen(), altaDTO.destino());
        Vuelo vuelo = new Vuelo(); 
        vuelo.setNumeroVuelo(altaDTO.numeroVuelo()); 
        vuelo.setOrigen(altaDTO.origen()); 
        vuelo.setDestino(altaDTO.destino()); 
        vuelo.setHoraSalida(altaDTO.horaSalida()); 
        vuelo.setHoraLlegada(altaDTO.horaLlegada()); 
        vuelo.setAvionId(altaDTO.avionId()); 
        vuelo.setEstado(altaDTO.estado() != null && !altaDTO.estado().trim().isEmpty() ? altaDTO.estado() : "PROGRAMADO");
        
        Vuelo guardado = vueloRepository.save(vuelo);
        log.info("Vuelo guardado con exito. ID: {}", guardado.getId());
        
        if (rabbitTemplate != null) {
            if (guardado.getAvionId() != null) {
                try { 
                    log.info("Notificando alta de vuelo a RabbitMQ (vuelo.alta.queue) | Vuelo ID: {}, Avion ID: {}", guardado.getId(), guardado.getAvionId());
                    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ALTA, 
                            new VueloEvent(guardado.getId().longValue(), guardado.getAvionId().longValue())); 
                } catch (Exception e) {
                    log.error("Error al enviar evento vuelo.alta.queue: {}", e.getMessage(), e);
                }
            }
            try {
                log.info("Notificando estado de vuelo a RabbitMQ (vuelo.estado.queue) | Vuelo ID: {}, Estado: {}", guardado.getId(), guardado.getEstado());
                rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ESTADO, 
                        new VueloEstadoEvent(guardado.getId().longValue(), guardado.getEstado()));
            } catch (Exception e) {
                log.error("Error al enviar evento vuelo.estado.queue: {}", e.getMessage(), e);
            }
        }
        return convertirAConAvionDTO(guardado);
    }

    @Override 
    public VueloConAvionDTO actualizar(Integer id, VueloAltaDTO altaDTO) {
        log.info("Actualizando vuelo con ID: {}", id);
        return vueloRepository.findById(id).map(vuelo -> {
            if (altaDTO.numeroVuelo() != null) vuelo.setNumeroVuelo(altaDTO.numeroVuelo()); 
            if (altaDTO.origen() != null) vuelo.setOrigen(altaDTO.origen()); 
            if (altaDTO.destino() != null) vuelo.setDestino(altaDTO.destino()); 
            if (altaDTO.horaSalida() != null) vuelo.setHoraSalida(altaDTO.horaSalida()); 
            if (altaDTO.horaLlegada() != null) vuelo.setHoraLlegada(altaDTO.horaLlegada()); 
            if (altaDTO.avionId() != null) vuelo.setAvionId(altaDTO.avionId());
            
            if (altaDTO.estado() != null && !altaDTO.estado().trim().isEmpty()) {
                vuelo.setEstado(altaDTO.estado());
            }
            Vuelo guardado = vueloRepository.save(vuelo);
            log.info("Vuelo ID {} actualizado exitosamente", id);

            if (rabbitTemplate != null) {
                try {
                    log.info("Notificando cambio de estado de vuelo a RabbitMQ (vuelo.estado.queue) | Vuelo ID: {}, Estado: {}", guardado.getId(), guardado.getEstado());
                    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ESTADO, 
                            new VueloEstadoEvent(guardado.getId().longValue(), guardado.getEstado()));
                } catch (Exception e) {
                    log.error("Error al enviar evento vuelo.estado.queue: {}", e.getMessage(), e);
                }
            }

            return convertirAConAvionDTO(guardado);
        }).orElseGet(() -> {
            log.warn("No se encontro el vuelo ID {} para actualizar", id);
            return null;
        });
    }

    @Override 
    public boolean eliminar(Integer id) {
        log.info("Procesando cancelacion de vuelo ID: {}", id);
        return vueloRepository.findById(id).map(vuelo -> { 
            Integer avionAnteriorId = vuelo.getAvionId();

            vuelo.setEstado("CANCELADO"); 
            vuelo.setAvionId(null); 
            vueloRepository.save(vuelo); 
            log.info("Vuelo ID {} marcado como CANCELADO", id);

            if (rabbitTemplate != null) {
                if (avionAnteriorId != null) {
                    try {
                        log.info("Notificando baja de vuelo a RabbitMQ (vuelo.baja.queue) | Vuelo ID: {}, Avion Anterior ID: {}", vuelo.getId(), avionAnteriorId);
                        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_BAJA, 
                                new VueloEvent(vuelo.getId().longValue(), avionAnteriorId.longValue()));
                    } catch (Exception e) {
                        log.error("Error al enviar evento vuelo.baja.queue: {}", e.getMessage(), e);
                    }
                }
                try {
                    log.info("Notificando cambio de estado de cancelacion a RabbitMQ (vuelo.estado.queue) | Vuelo ID: {}", vuelo.getId());
                    rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_VUELO_ESTADO, 
                            new VueloEstadoEvent(vuelo.getId().longValue(), "CANCELADO"));
                } catch (Exception e) {
                    log.error("Error al enviar evento vuelo.estado.queue: {}", e.getMessage(), e);
                }
            }

            return true; 
        }).orElseGet(() -> {
            log.warn("No se encontro el vuelo ID {} para cancelar", id);
            return false;
        });
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
package com.terminal.aviones_service.listener;

import com.terminal.aviones_service.event.VueloEvent;
import com.terminal.aviones_service.repository.AvionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AvionListener {

    @Autowired
    private AvionRepository avionRepository;

    @RabbitListener(queues = "vuelo.alta.queue")
    public void recibirAltaVuelo(VueloEvent evento) {
        log.info("[RabbitMQ - Evento Alta Vuelo] Vuelo ID: {} asignado al Avión ID: {}", evento.getIdVuelo(), evento.getIdAvion());

        if (evento.getIdAvion() != null) {
            avionRepository.findById(evento.getIdAvion().intValue()).ifPresent(avion -> {
                if (avion.getCapacidadPersonas() != null && avion.getCapacidadPersonas() > 0) {
                    avion.setCapacidadPersonas(avion.getCapacidadPersonas() - 1);
                    avionRepository.save(avion);
                    log.info("[RabbitMQ] Asiento reservado en avión ID {}. Capacidad de asientos restante: {}", avion.getId(), avion.getCapacidadPersonas());
                } else {
                    log.warn("[RabbitMQ] No se pudo reservar asiento en avión ID {}, capacidad agotada o nula", avion.getId());
                }
            });
        }
    }

    @RabbitListener(queues = "vuelo.baja.queue")
    public void recibirBajaVuelo(VueloEvent evento) {
        log.info("[RabbitMQ - Evento Baja Vuelo] Vuelo ID: {} removido del Avión ID: {}", evento.getIdVuelo(), evento.getIdAvion());

        if (evento.getIdAvion() != null) {
            avionRepository.findById(evento.getIdAvion().intValue()).ifPresent(avion -> {
                if (avion.getCapacidadPersonas() != null) {
                    avion.setCapacidadPersonas(avion.getCapacidadPersonas() + 1);
                    avionRepository.save(avion);
                    log.info("[RabbitMQ] Asiento liberado en avión ID {}. Nueva capacidad disponible: {}", avion.getId(), avion.getCapacidadPersonas());
                }
            });
        }
    }
}
package com.terminal.personal_service.listener;

import com.terminal.personal_service.event.VueloEstadoEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class AuditListener {

    @RabbitListener(queues = "vuelo.estado.queue")
    public void recibirCambioEstado(VueloEstadoEvent evento) {
        log.info("[RabbitMQ - Auditoría Vuelos] Evento recibido | Vuelo ID: {} | Nuevo Estado: {}", 
                 evento.getIdVuelo(), evento.getEstado());
    }
}
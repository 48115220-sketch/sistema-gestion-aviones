package com.escuela.admin_service.listener;

import com.escuela.admin_service.event.AlumnoEstadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

    @RabbitListener(queues = "alumno.estado.queue")
    public void recibirCambioEstado(AlumnoEstadoEvent evento) {
        System.out.println(">>> [RabbitMQ - Auditoria Admin] Registro de auditoria recibido. Alumno ID: " 
                + evento.getIdAlumno() + " -> Nuevo Estado: " + evento.getEstado());
    }
}
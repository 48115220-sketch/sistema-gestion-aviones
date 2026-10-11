package com.escuela.curso_service.listener;

import com.escuela.curso_service.event.AlumnoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CursoListener {

    @RabbitListener(queues = "alumno.alta.queue")
    public void recibirAltaAlumno(AlumnoEvent evento) {
        System.out.println(">>> Evento recibido en curso-service para Alumno ID: " 
                + evento.getIdAlumno() + " asignado al Curso ID: " + evento.getIdCurso());
    }
}

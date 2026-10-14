package com.escuela.curso_service.listener;

import com.escuela.curso_service.event.AlumnoEvent;
import com.escuela.curso_service.repository.CursoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CursoListener {

    @Autowired
    private CursoRepository cursoRepository;

    @RabbitListener(queues = "alumno.alta.queue")
    public void recibirAltaAlumno(AlumnoEvent evento) {
        System.out.println(">>> [RabbitMQ - Evento Alta] Alumno ID: " 
                + evento.getIdAlumno() + " asignado al Curso ID: " + evento.getIdCurso());

        if (evento.getIdCurso() != null) {
            cursoRepository.findById(evento.getIdCurso().intValue()).ifPresent(curso -> {
                if (curso.getCupo() != null && curso.getCupo() > 0) {
                    curso.setCupo(curso.getCupo() - 1);
                    cursoRepository.save(curso);
                    System.out.println(">>> [RabbitMQ] Cupo descontado. Vacantes restantes: " + curso.getCupo());
                }
            });
        }
    }

    @RabbitListener(queues = "alumno.baja.queue")
    public void recibirBajaAlumno(AlumnoEvent evento) {
        System.out.println(">>> [RabbitMQ - Evento Baja] Alumno ID: " 
                + evento.getIdAlumno() + " removido del Curso ID: " + evento.getIdCurso());

        if (evento.getIdCurso() != null) {
            cursoRepository.findById(evento.getIdCurso().intValue()).ifPresent(curso -> {
                if (curso.getCupo() != null) {
                    curso.setCupo(curso.getCupo() + 1);
                    cursoRepository.save(curso);
                    System.out.println(">>> [RabbitMQ] Vacante liberada. Nuevas vacantes: " + curso.getCupo());
                }
            });
        }
    }
}
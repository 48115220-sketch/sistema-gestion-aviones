package com.escuela.alumno_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_ALUMNO_ALTA = "alumno.alta.queue";
    public static final String QUEUE_ALUMNO_BAJA = "alumno.baja.queue";
    public static final String QUEUE_ALUMNO_ESTADO = "alumno.estado.queue";

    @Bean public Queue altaQueue() { return new Queue(QUEUE_ALUMNO_ALTA, true); }
    @Bean public Queue bajaQueue() { return new Queue(QUEUE_ALUMNO_BAJA, true); }
    @Bean public Queue estadoQueue() { return new Queue(QUEUE_ALUMNO_ESTADO, true); }

    @Bean public MessageConverter jsonMessageConverter() { return new Jackson2JsonMessageConverter(); }
}
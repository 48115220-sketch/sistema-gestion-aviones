package com.terminal.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String metodo = request.getMethod() != null ? request.getMethod().name() : "DESCONOCIDO";
        String uri = request.getURI().getPath();

        log.info("Peticion entrante en Gateway -> [{}] {}", metodo, uri);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            var response = exchange.getResponse();
            var statusCode = response.getStatusCode();
            log.info("Respuesta en Gateway -> [{}] {} | Status: {}", metodo, uri, statusCode);
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
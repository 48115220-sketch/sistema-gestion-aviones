package com.terminal.aviones_service.client;

import com.terminal.aviones_service.dto.VueloDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "vuelos-service")
public interface VueloClient {

    @GetMapping("/vuelos")
    List<VueloDTO> obtenerTodosVuelos(@RequestHeader(value = "Authorization", required = false) String token);
}
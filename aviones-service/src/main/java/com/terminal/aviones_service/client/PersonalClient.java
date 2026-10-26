package com.terminal.aviones_service.client;

import com.terminal.aviones_service.dto.PersonalDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "personal-service")
public interface PersonalClient {

    @GetMapping("/admin/personal/{id}")
    PersonalDTO obtenerPersonalPorId(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable("id") Integer id);
}
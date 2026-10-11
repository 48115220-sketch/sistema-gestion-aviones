package com.escuela.curso_service.client;
import com.escuela.curso_service.dto.AlumnoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(name = "alumno-service")
public interface AlumnoClient {

    @GetMapping("/alumnos") List<AlumnoDTO> obtenerTodosAlumnos(@RequestHeader(value = "Authorization", required = false) String token);
    
}

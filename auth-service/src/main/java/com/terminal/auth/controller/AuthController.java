package com.terminal.auth.controller;

import com.terminal.auth.dto.*;
import com.terminal.auth.service.AuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody RegisterRequest request) {
        log.info("Solicitud recibida en /auth/register para usuario: {}", request.nombreUsuario());
        try {
            String mensaje = authService.registrar(request);
            log.info("Registro exitoso para usuario: {}", request.nombreUsuario());
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
        } catch (IllegalArgumentException e) {
            log.warn("Fallo en registro para usuario {}: {}", request.nombreUsuario(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String usuarioLog = (request != null) ? request.nombreUsuario() : "NULO";
        log.info("Solicitud recibida en /auth/login para usuario: {}", usuarioLog);
        try {
            LoginResponse response = authService.login(request);
            log.info("Login exitoso para usuario: {}", usuarioLog);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Autenticación fallida para usuario {}: {}", usuarioLog, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error no esperado durante login para usuario {}: {}", usuarioLog, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en autenticacion: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Solicitud recibida en /auth/refresh");
        try {
            LoginResponse response = authService.refreshToken(request);
            log.info("Token renovado exitosamente para usuario: {}", response.nombreUsuario());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Fallo al renovar token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
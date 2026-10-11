package com.escuela.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String claveSecreta;

    private final long TIEMPO_EXPIRACION = 86400000;

    private Key getLlaveFirma() {
        return Keys.hmacShaKeyFor(claveSecreta.getBytes(StandardCharsets.UTF_8));
    }

    public String generarToken(String nombreUsuario, String rol) {
        String rolFormateado = (rol != null && !rol.startsWith("ROLE_")) ? "ROLE_" + rol : rol;
        if (rolFormateado == null) rolFormateado = "ROLE_USER";

        return Jwts.builder()
                .setSubject(nombreUsuario)
                .claim("roles", List.of(rolFormateado))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION))
                .signWith(getLlaveFirma(), SignatureAlgorithm.HS256)
                .compact();
    }
}
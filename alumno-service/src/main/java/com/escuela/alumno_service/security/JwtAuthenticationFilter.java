package com.escuela.alumno_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (!token.equalsIgnoreCase("null") && !token.equalsIgnoreCase("undefined") && !token.trim().isEmpty()) {
                    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

                    String username = claims.getSubject();
                    Object rolesObject = claims.get("roles");
                    if (rolesObject == null) {
                        rolesObject = claims.get("role");
                    }

                    if (username != null) {
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        if (rolesObject instanceof List<?>) {
                            for (Object r : (List<?>) rolesObject) {
                                String rStr = r.toString();
                                authorities.add(new SimpleGrantedAuthority(rStr.startsWith("ROLE_") ? rStr : "ROLE_" + rStr));
                            }
                        } else if (rolesObject instanceof String) {
                            String rStr = (String) rolesObject;
                            authorities.add(new SimpleGrantedAuthority(rStr.startsWith("ROLE_") ? rStr : "ROLE_" + rStr));
                        } else {
                            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        }

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                System.err.println(">>> [ALUMNO-SERVICE] ERROR JWT: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
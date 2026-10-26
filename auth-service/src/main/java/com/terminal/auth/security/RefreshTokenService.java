package com.terminal.auth.security;

import com.terminal.auth.model.RefreshToken;
import com.terminal.auth.model.Usuario;
import com.terminal.auth.repository.RefreshTokenRepository;
import com.terminal.auth.repository.UsuarioRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final Long EXPIRACION_REFRESH_MS = 604800000L;

    @Transactional
    public RefreshToken crearRefreshToken(String nombreUsuario) {
        log.debug("Generando Refresh Token para usuario: {}", nombreUsuario);
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> {
                    log.error("Error al generar Refresh Token: Usuario no encontrado ({})", nombreUsuario);
                    return new RuntimeException("Usuario no encontrado: " + nombreUsuario);
                });

        refreshTokenRepository.findByUsuario(usuario).ifPresent(token -> {
            log.debug("Eliminando Refresh Token previo para usuario: {}", nombreUsuario);
            refreshTokenRepository.delete(token);
            refreshTokenRepository.flush();
        });

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .fechaExpiracion(Instant.now().plusMillis(EXPIRACION_REFRESH_MS))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verificarExpiracion(RefreshToken token) {
        if (token.getFechaExpiracion().isBefore(Instant.now())) {
            log.warn("Refresh Token expirado para usuario: {}", token.getUsuario().getNombreUsuario());
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado.");
        }
        return token;
    }
}
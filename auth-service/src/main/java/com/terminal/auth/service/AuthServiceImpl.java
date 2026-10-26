package com.terminal.auth.service;

import com.terminal.auth.dto.*;
import com.terminal.auth.model.RefreshToken;
import com.terminal.auth.model.Usuario;
import com.terminal.auth.repository.UsuarioRepository;
import com.terminal.auth.security.JwtService;
import com.terminal.auth.security.RefreshTokenService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public String registrar(RegisterRequest request) {
        log.info("Procesando registro para usuario: {}", request.nombreUsuario());
        if (usuarioRepository.existsByNombreUsuario(request.nombreUsuario())) {
            log.warn("Intento de registro duplicado para usuario: {}", request.nombreUsuario());
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        String rolFinal = request.rol() != null ? request.rol().toUpperCase() : "DOCENTE";

        Usuario usuario = Usuario.builder()
                .nombreUsuario(request.nombreUsuario())
                .clave(passwordEncoder.encode(request.clave()))
                .correo(request.correo())
                .rol(rolFinal)
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario {} registrado exitosamente con rol: {}", request.nombreUsuario(), rolFinal);
        return "Usuario registrado con exito";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.nombreUsuario() == null || request.clave() == null){
            log.warn("Intento de login con payload o datos nulos");
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        log.info("Validando credenciales para usuario: {}", request.nombreUsuario());
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.nombreUsuario()).orElse(null);

        if (usuario == null || usuario.getClave() == null || !passwordEncoder.matches(request.clave(), usuario.getClave())) {
            log.warn("Credenciales inválidas para usuario: {}", request.nombreUsuario());
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        String jwtToken = jwtService.generarToken(usuario.getNombreUsuario(), usuario.getRol());
        RefreshToken refreshToken = refreshTokenService.crearRefreshToken(usuario.getNombreUsuario());

        log.info("Autenticación y generación de tokens completada para usuario: {}", usuario.getNombreUsuario());
        return new LoginResponse(jwtToken, refreshToken.getToken(), usuario.getNombreUsuario());
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null) {
            log.warn("Solicitud de Refresh Token vacía o inválida");
            throw new IllegalArgumentException("Refresh Token no valido");
        }

        Optional<RefreshToken> tokenOptional = refreshTokenService.findByToken(request.refreshToken());

        if (tokenOptional.isEmpty()) {
            log.warn("Refresh Token inexistente en base de datos");
            throw new IllegalArgumentException("Refresh Token no valido");
        }

        RefreshToken refreshToken = refreshTokenService.verificarExpiracion(tokenOptional.get());
        Usuario usuario = refreshToken.getUsuario();
        String nuevoToken = jwtService.generarToken(usuario.getNombreUsuario(), usuario.getRol());

        log.info("Nuevo JWT emitido via Refresh Token para usuario: {}", usuario.getNombreUsuario());
        return new LoginResponse(nuevoToken, request.refreshToken(), usuario.getNombreUsuario());
    }
}
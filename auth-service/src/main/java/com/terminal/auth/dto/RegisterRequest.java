package com.terminal.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
    @JsonProperty("username") @JsonAlias({"nombreUsuario", "user"}) String nombreUsuario,
    @JsonProperty("password") @JsonAlias({"clave", "contrasena"}) String clave,
    String correo,
    String rol
) {}

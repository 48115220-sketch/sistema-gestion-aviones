package com.terminal.auth.service;

import com.terminal.auth.dto.*;

public interface AuthService {
    String registrar(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
}
package com.ing.walletapi.service;

import com.ing.walletapi.dto.JwtAuthenticationResponse;
import com.ing.walletapi.dto.LoginRequest;
import com.ing.walletapi.dto.RegisterRequest;

public interface AuthService {
    JwtAuthenticationResponse register(RegisterRequest request);
    JwtAuthenticationResponse login(LoginRequest request);
}

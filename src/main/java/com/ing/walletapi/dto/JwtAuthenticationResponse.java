package com.ing.walletapi.dto;

import com.ing.walletapi.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private Role role;
}

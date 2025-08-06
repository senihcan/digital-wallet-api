package com.ing.walletapi.dto;

import com.ing.walletapi.entity.Role;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;
    private String name;
    private String surname;
    private String tckn;
}

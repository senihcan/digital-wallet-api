package com.ing.walletapi.dto;

import com.ing.walletapi.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegisterRequest {

    @Schema(description = "Kullanıcı adı", example = "john_doe")
    private String username;

    @Schema(description = "Kullanıcı şifresi", example = "securePassword123")
    private String password;

    @Schema(
        description = "Kullanıcı rolü. Sadece iki değer geçerlidir: CUSTOMER veya EMPLOYEE",
        example = "CUSTOMER",
        allowableValues = {"CUSTOMER", "EMPLOYEE"}
    )
    private Role role;

    @Schema(description = "Adı", example = "John")
    private String name;

    @Schema(description = "Soyadı", example = "Doe")
    private String surname;

    @Schema(description = "TCKN (Türkiye Cumhuriyeti Kimlik Numarası)", example = "12345678901")
    private String tckn;
}

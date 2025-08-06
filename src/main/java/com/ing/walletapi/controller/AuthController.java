package com.ing.walletapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ing.walletapi.dto.JwtAuthenticationResponse;
import com.ing.walletapi.dto.LoginRequest;
import com.ing.walletapi.dto.RegisterRequest;
import com.ing.walletapi.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@Operation(
		summary = "Register a new user",
		description = "Allows a new user to register as CUSTOMER or EMPLOYEE. Returns a JWT token upon successful registration."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "User registered successfully",
			content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
		@ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
	})
	@PostMapping("/register")
	public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	@Operation(
		summary = "User login",
		description = "Authenticates a registered user and returns a JWT token on success."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Login successful",
			content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
		@ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
	})
	@PostMapping("/login")
	public ResponseEntity<JwtAuthenticationResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}
}

package com.ing.walletapi.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ing.walletapi.dto.JwtAuthenticationResponse;
import com.ing.walletapi.dto.LoginRequest;
import com.ing.walletapi.dto.RegisterRequest;
import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.Role;
import com.ing.walletapi.entity.User;
import com.ing.walletapi.exception.WalletOperationException;
import com.ing.walletapi.security.JwtUtil;
import com.ing.walletapi.service.AuthService;
import com.ing.walletapi.service.CustomerService;
import com.ing.walletapi.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserService userService;
	private final CustomerService customerService;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	@Override
	public JwtAuthenticationResponse register(RegisterRequest request) {
		if (userService.findByUsername(request.getUsername()).isPresent()) {
			throw new WalletOperationException("Username already exists");
		}
		Role role = request.getRole() == null ? Role.CUSTOMER : request.getRole();
		User user = User.builder().username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword())).role(role).build();
		userService.save(user);

		if (role == Role.CUSTOMER) {
			Customer customer = Customer.builder().user(user).name(request.getName()).surname(request.getSurname())
					.tckn(request.getTckn()).build();
			customerService.save(customer);
		}
		String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
		return new JwtAuthenticationResponse(token, user.getRole());
	}

	@Override
	public JwtAuthenticationResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		User user = userService.findByUsername(request.getUsername()).orElseThrow();
		String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
		return new JwtAuthenticationResponse(token, user.getRole());
	}
}

package com.ing.walletapi.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ing.walletapi.entity.User;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    User save(User user);
    Optional<User> findByUsername(String username);
}

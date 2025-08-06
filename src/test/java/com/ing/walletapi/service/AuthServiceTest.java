package com.ing.walletapi.service;

import com.ing.walletapi.dto.JwtAuthenticationResponse;
import com.ing.walletapi.dto.LoginRequest;
import com.ing.walletapi.dto.RegisterRequest;
import com.ing.walletapi.entity.Role;
import com.ing.walletapi.exception.WalletOperationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
 class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testSuccessfulCustomerRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("auth_user1");
        request.setPassword("pass");
        request.setName("Ali");
        request.setSurname("Veli");
        request.setTckn("10000000001");
        request.setRole(Role.CUSTOMER);

        JwtAuthenticationResponse response = authService.register(request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getToken());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void testRegistrationWithExistingUsernameThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("auth_user2");
        request.setPassword("pass");
        request.setName("Ali");
        request.setSurname("Veli");
        request.setTckn("10000000002");
        request.setRole(Role.CUSTOMER);

        authService.register(request);

        Assertions.assertThrows(WalletOperationException.class, () -> {
            authService.register(request);
        });
    }

    @Test
    void testDefaultRoleIsCustomerWhenNull() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("auth_user3");
        request.setPassword("pass");
        request.setName("Ali");
        request.setSurname("Veli");
        request.setTckn("10000000003");
        request.setRole(null);

        JwtAuthenticationResponse response = authService.register(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void testSuccessfulLogin() {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("auth_user4");
        reg.setPassword("pass");
        reg.setName("Ali");
        reg.setSurname("Veli");
        reg.setTckn("10000000004");
        reg.setRole(Role.CUSTOMER);
        authService.register(reg);

        LoginRequest login = new LoginRequest();
        login.setUsername("auth_user4");
        login.setPassword("pass");

        JwtAuthenticationResponse response = authService.login(login);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getToken());
        Assertions.assertEquals(Role.CUSTOMER, response.getRole());
    }

    @Test
    void testLoginWithWrongPasswordThrowsException() {
        RegisterRequest reg = new RegisterRequest();
        reg.setUsername("auth_user5");
        reg.setPassword("correctpass");
        reg.setName("Ali");
        reg.setSurname("Veli");
        reg.setTckn("10000000005");
        reg.setRole(Role.CUSTOMER);
        authService.register(reg);

        LoginRequest login = new LoginRequest();
        login.setUsername("auth_user5");
        login.setPassword("wrongpass");

        Assertions.assertThrows(Exception.class, () -> {
            authService.login(login);
        });
    }
}

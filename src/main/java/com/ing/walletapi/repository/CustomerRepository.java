package com.ing.walletapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.User;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
}

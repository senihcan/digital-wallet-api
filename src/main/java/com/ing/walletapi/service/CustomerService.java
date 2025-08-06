package com.ing.walletapi.service;

import java.util.Optional;

import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.User;

public interface CustomerService {
    Customer save(Customer customer);
    Optional<Customer> findByUser(User user);
}

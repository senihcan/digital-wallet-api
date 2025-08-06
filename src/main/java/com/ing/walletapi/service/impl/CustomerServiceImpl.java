package com.ing.walletapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.User;
import com.ing.walletapi.repository.CustomerRepository;
import com.ing.walletapi.service.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;

	@Override
	public Customer save(Customer customer) {
		return customerRepository.save(customer);
	}

	@Override
	public Optional<Customer> findByUser(User user) {
		return customerRepository.findByUser(user);
	}
}

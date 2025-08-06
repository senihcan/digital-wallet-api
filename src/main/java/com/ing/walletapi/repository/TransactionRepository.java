package com.ing.walletapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.entity.TransactionStatus;
import com.ing.walletapi.entity.Wallet;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
    List<Transaction> findByStatus(TransactionStatus status);

}

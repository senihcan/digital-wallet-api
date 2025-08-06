package com.ing.walletapi.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ing.walletapi.dto.TransactionApprovalRequest;
import com.ing.walletapi.dto.TransactionRequest;
import com.ing.walletapi.dto.WalletRequest;
import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.entity.Wallet;

public interface WalletService {
    Wallet createWallet(WalletRequest request, String username);
    Page<Wallet> listWallets(String username, Wallet request, Pageable pageablee);
    Transaction deposit(TransactionRequest request, String username);
    Transaction withdraw(TransactionRequest request, String username);
    List<Transaction> listTransactions(Long walletId, String username);
    Transaction approve(TransactionApprovalRequest request, String username);
    List<Transaction> listPendingTransactionsForApproval(String username);

}

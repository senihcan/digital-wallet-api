package com.ing.walletapi.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ing.walletapi.dto.TransactionApprovalRequest;
import com.ing.walletapi.dto.TransactionRequest;
import com.ing.walletapi.dto.WalletRequest;
import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.Role;
import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.entity.TransactionStatus;
import com.ing.walletapi.entity.TransactionType;
import com.ing.walletapi.entity.User;
import com.ing.walletapi.entity.Wallet;
import com.ing.walletapi.exception.WalletOperationException;
import com.ing.walletapi.repository.TransactionRepository;
import com.ing.walletapi.repository.WalletRepository;
import com.ing.walletapi.service.CustomerService;
import com.ing.walletapi.service.UserService;
import com.ing.walletapi.service.WalletService;
import com.ing.walletapi.specification.WalletSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

	private final WalletRepository walletRepository;

	private final TransactionRepository transactionRepository;

	private final UserService userService;

	private final CustomerService customerService;

	private Wallet getWallet(Long walletId) {
		return walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));
	}

	@Override
	public Wallet createWallet(WalletRequest request, String username) {
		User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		if (user.getRole() != Role.CUSTOMER) {
			throw new WalletOperationException("Only customers can create wallets");
		}
		Customer customer = customerService.findByUser(user)
				.orElseThrow(() -> new WalletOperationException("Customer not found"));
		Wallet wallet = Wallet.builder().customer(customer).walletName(request.getWalletName())
				.currency(request.getCurrency()).activeForShopping(request.getActiveForShopping())
				.activeForWithdraw(request.getActiveForWithdraw()).balance(BigDecimal.ZERO)
				.usableBalance(BigDecimal.ZERO).build();
		return walletRepository.save(wallet);
	}

	@Override
	public Page<Wallet> listWallets(String username, Wallet request, Pageable pageable) {
		 User user = userService.findByUsername(username).orElseThrow(() -> new WalletOperationException("User not found"));
		    Customer customer = null;
		    if (user.getRole() == Role.CUSTOMER) {
		        customer = customerService.findByUser(user).orElseThrow(() -> new WalletOperationException("Customer not found"));
		    }
		    Specification<Wallet> spec = WalletSpecification.buildSpecification(
		        customer,
		        request.getCurrency(),
		        request.getWalletName(),
		        request.getActiveForShopping(),
		        request.getActiveForWithdraw(),
		        request.getBalance()
		    );

		    return walletRepository.findAll(spec, pageable);
	}

	@Override
	@Transactional
	public Transaction deposit(TransactionRequest request, String username) {
		
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
	        throw new WalletOperationException("Withdraw amount must be greater than zero");
	    }
		Wallet wallet = getWallet(request.getWalletId());
		User user = userService.findByUsername(username).orElseThrow();
		if (user.getRole() == Role.CUSTOMER) {
			Customer customer = customerService.findByUser(user).orElseThrow();
			if (!wallet.getCustomer().getId().equals(customer.getId())) {
				throw new WalletOperationException("Unauthorized");
			}
		}
		Transaction transaction = Transaction.builder().wallet(wallet).amount(request.getAmount())
				.type(TransactionType.DEPOSIT).oppositePartyType(request.getOppositePartyType())
				.oppositeParty(request.getOppositeParty())
				.status(request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0 ? TransactionStatus.PENDING
						: TransactionStatus.APPROVED)
				.createdAt(LocalDateTime.now()).build();
		if (transaction.getStatus() == TransactionStatus.APPROVED) {
			wallet.setBalance(wallet.getBalance().add(request.getAmount()));
			wallet.setUsableBalance(wallet.getUsableBalance().add(request.getAmount()));
			walletRepository.save(wallet);
		} else {
			wallet.setBalance(wallet.getBalance().add(request.getAmount()));
			walletRepository.save(wallet);
		}
		return transactionRepository.save(transaction);
	}

	@Override
	@Transactional
	public Transaction withdraw(TransactionRequest request, String username) {
		
		if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
	        throw new WalletOperationException("Withdraw amount must be greater than zero");
	    }
		
		Wallet wallet = getWallet(request.getWalletId());
		if (!wallet.getActiveForWithdraw()) {
			throw new WalletOperationException("Wallet not active for withdraw");
		}
		User user = userService.findByUsername(username).orElseThrow();
		if (user.getRole() == Role.CUSTOMER) {
			Customer customer = customerService.findByUser(user).orElseThrow();
			if (!wallet.getCustomer().getId().equals(customer.getId())) {
				throw new WalletOperationException("Unauthorized");
			}
		}
		if (wallet.getUsableBalance().compareTo(request.getAmount()) < 0) {
			throw new WalletOperationException("Insufficient usable balance");
		}
		Transaction transaction = Transaction.builder().wallet(wallet).amount(request.getAmount())
				.type(TransactionType.WITHDRAW).oppositePartyType(request.getOppositePartyType())
				.oppositeParty(request.getOppositeParty())
				.status(request.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0 ? TransactionStatus.PENDING
						: TransactionStatus.APPROVED)
				.createdAt(LocalDateTime.now()).build();
		if (transaction.getStatus() == TransactionStatus.APPROVED) {
			wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
			wallet.setUsableBalance(wallet.getUsableBalance().subtract(request.getAmount()));
			walletRepository.save(wallet);
		} else {
			wallet.setUsableBalance(wallet.getUsableBalance().subtract(request.getAmount()));
			walletRepository.save(wallet);
		}
		return transactionRepository.save(transaction);
	}

	@Override
	public List<Transaction> listTransactions(Long walletId, String username) {
		Wallet wallet = getWallet(walletId);
		User user = userService.findByUsername(username).orElseThrow();
		if (user.getRole() == Role.CUSTOMER) {
			Customer customer = customerService.findByUser(user).orElseThrow();
			if (!wallet.getCustomer().getId().equals(customer.getId())) {
				throw new WalletOperationException("Unauthorized");
			}
		}
		return transactionRepository.findByWallet(wallet);
	}

	@Override
	@Transactional
	public Transaction approve(TransactionApprovalRequest request, String username) {
		User user = userService.findByUsername(username).orElseThrow();
		if (user.getRole() != Role.EMPLOYEE) {
			throw new WalletOperationException("Only employees can approve transactions");
		}
		Transaction transaction = transactionRepository.findById(request.getTransactionId())
				.orElseThrow(() -> new RuntimeException("Transaction not found"));
		if (transaction.getStatus() != TransactionStatus.PENDING) {
			throw new WalletOperationException("Only pending transactions can be approved/denied");
		}
		Wallet wallet = transaction.getWallet();
		if ("APPROVED".equalsIgnoreCase(request.getStatus())) {
			transaction.setStatus(TransactionStatus.APPROVED);
			if (transaction.getType() == TransactionType.DEPOSIT) {
				wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
			} else if (transaction.getType() == TransactionType.WITHDRAW) {
				wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
			}
		} else if ("DENIED".equalsIgnoreCase(request.getStatus())) {
			transaction.setStatus(TransactionStatus.DENIED);
			if (transaction.getType() == TransactionType.DEPOSIT) {
				wallet.setBalance(wallet.getBalance().subtract(transaction.getAmount()));
			} else if (transaction.getType() == TransactionType.WITHDRAW) {
				wallet.setUsableBalance(wallet.getUsableBalance().add(transaction.getAmount()));
			}
		} else {
			throw new WalletOperationException("Invalid status");
		}
		walletRepository.save(wallet);
		return transactionRepository.save(transaction);
	}
	
	@Override
	public List<Transaction> listPendingTransactionsForApproval(String username) {
		User user = userService.findByUsername(username).orElseThrow();
		if (user.getRole() != Role.EMPLOYEE) {
				throw new WalletOperationException("Unauthorized");
			
		}
		return transactionRepository.findByStatus(TransactionStatus.PENDING);
	}
}

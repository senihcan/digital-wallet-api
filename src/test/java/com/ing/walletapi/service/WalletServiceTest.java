package com.ing.walletapi.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ing.walletapi.dto.RegisterRequest;
import com.ing.walletapi.dto.TransactionApprovalRequest;
import com.ing.walletapi.dto.TransactionRequest;
import com.ing.walletapi.dto.WalletRequest;
import com.ing.walletapi.entity.OppositePartyType;
import com.ing.walletapi.entity.Role;
import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.entity.TransactionStatus;
import com.ing.walletapi.entity.Wallet;
import com.ing.walletapi.exception.WalletOperationException;

import jakarta.transaction.Transactional;

@SpringBootTest
 class WalletServiceTest {

	@Autowired
	private WalletService walletService;

	@Autowired
	private AuthService authService;

	private Wallet registerAndCreateWallet(String username) {
		RegisterRequest reg = new RegisterRequest();
		reg.setUsername(username);
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("Test");
		reg.setSurname("User");
		reg.setTckn("12345678901");
		authService.register(reg);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setWalletName("Main Wallet");
		walletRequest.setCurrency("TRY");
		walletRequest.setActiveForShopping(true);
		walletRequest.setActiveForWithdraw(true);

		return walletService.createWallet(walletRequest, username);
	}

	@Test
	@Transactional
	 void testCreateWalletAndListWithSearch() {
		String username = "user_search";
		registerAndCreateWallet(username);

		Wallet searchRequest = new Wallet();
		searchRequest.setWalletName("Main");
		searchRequest.setCurrency("TRY");
		searchRequest.setActiveForShopping(true);
		searchRequest.setActiveForWithdraw(true);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Wallet> result = walletService.listWallets(username, searchRequest, pageable);
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	@Transactional
	 void testDepositAndWithdraw() {
		String username = "user_transaction";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("500"));
		deposit.setOppositePartyType(OppositePartyType.IBAN);
		deposit.setOppositeParty("TR0000000000000000");

		Transaction depResult = walletService.deposit(deposit, username);
		Assertions.assertEquals(TransactionStatus.APPROVED, depResult.getStatus());

		TransactionRequest withdraw = new TransactionRequest();
		withdraw.setWalletId(wallet.getId());
		withdraw.setAmount(new BigDecimal("300"));
		withdraw.setOppositePartyType(OppositePartyType.IBAN);
		withdraw.setOppositeParty("TR0000000000000000");

		Transaction withResult = walletService.withdraw(withdraw, username);
		Assertions.assertEquals(TransactionStatus.APPROVED, withResult.getStatus());
	}

	@Test
	@Transactional
	 void testApproveTransaction() {
		String username = "user_approve";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("1200"));
		deposit.setOppositePartyType(OppositePartyType.IBAN);
		deposit.setOppositeParty("TR0000000000000000");
		Transaction txn = walletService.deposit(deposit, username);

		RegisterRequest empReg = new RegisterRequest();
		empReg.setUsername("employee1");
		empReg.setPassword("pass");
		empReg.setRole(Role.EMPLOYEE);
		empReg.setName("Emp");
		empReg.setSurname("Loyee");
		empReg.setTckn("22222222222");
		authService.register(empReg);

		TransactionApprovalRequest approveRequest = new TransactionApprovalRequest();
		approveRequest.setTransactionId(txn.getId());
		approveRequest.setStatus("APPROVED");
		Transaction result = walletService.approve(approveRequest, "employee1");

		Assertions.assertEquals(TransactionStatus.APPROVED, result.getStatus());
	}

	@Test
	@Transactional
	 void testListTransactions() {
		String username = "user_txnlist";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("900"));
		deposit.setOppositePartyType(OppositePartyType.IBAN);
		deposit.setOppositeParty("TR0000000000000000");
		walletService.deposit(deposit, username);

		List<Transaction> txns = walletService.listTransactions(wallet.getId(), username);
		Assertions.assertFalse(txns.isEmpty());
	}

	@Test
	@Transactional
	 void testDenyTransaction() {
		String username = "user_deny";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("1500"));
		deposit.setOppositePartyType(OppositePartyType.IBAN);
		deposit.setOppositeParty("TR0000000000000000");
		Transaction txn = walletService.deposit(deposit, username);

		RegisterRequest empReg = new RegisterRequest();
		empReg.setUsername("employee2");
		empReg.setPassword("pass");
		empReg.setRole(Role.EMPLOYEE);
		empReg.setName("Emp");
		empReg.setSurname("Loyee");
		empReg.setTckn("33333333333");
		authService.register(empReg);

		TransactionApprovalRequest denyRequest = new TransactionApprovalRequest();
		denyRequest.setTransactionId(txn.getId());
		denyRequest.setStatus("DENIED");
		Transaction result = walletService.approve(denyRequest, "employee2");

		Assertions.assertEquals(TransactionStatus.DENIED, result.getStatus());
	}

	@Test
	@Transactional
	 void testWithdrawNegativeAmountShouldFail() {
		String username = "neg_user";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest request = new TransactionRequest();
		request.setWalletId(wallet.getId());
		request.setAmount(new BigDecimal("-1"));
		request.setOppositePartyType(OppositePartyType.IBAN);
		request.setOppositeParty("TR123");

		Assertions.assertThrows(WalletOperationException.class, () -> {
			walletService.withdraw(request, username);
		});
	}

	@Test
	@Transactional
	 void testDepositNegativeAmountShouldFail() {
		String username = "neg_deposit";
		Wallet wallet = registerAndCreateWallet(username);

		TransactionRequest request = new TransactionRequest();
		request.setWalletId(wallet.getId());
		request.setAmount(new BigDecimal("-100"));
		request.setOppositePartyType(OppositePartyType.IBAN);
		request.setOppositeParty("TR123");

		Assertions.assertThrows(WalletOperationException.class, () -> {
			walletService.deposit(request, username);
		});
	}

	@Test
	@Transactional
	 void testWithdrawUnauthorizedAccess() {
		Wallet wallet = registerAndCreateWallet("owner");

		RegisterRequest reg = new RegisterRequest();
		reg.setUsername("unauthorized");
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("unauth");
		reg.setSurname("user");
		reg.setTckn("11112222333");
		authService.register(reg);

		TransactionRequest withdraw = new TransactionRequest();
		withdraw.setWalletId(wallet.getId());
		withdraw.setAmount(new BigDecimal("100"));
		withdraw.setOppositePartyType(OppositePartyType.IBAN);
		withdraw.setOppositeParty("TR123");

		Assertions.assertThrows(WalletOperationException.class, () -> {
			walletService.withdraw(withdraw, "unauthorized");
		});
	}

	@Test
	@Transactional
	 void testCustomerCannotApproveTransaction() {
	    String username = "customer_approve";
	    Wallet wallet = registerAndCreateWallet(username);

	    TransactionRequest deposit = new TransactionRequest();
	    deposit.setWalletId(wallet.getId());
	    deposit.setAmount(new BigDecimal("1500")); 	    deposit.setOppositePartyType(OppositePartyType.IBAN);
	    deposit.setOppositeParty("TR0000000000000000");
	    Transaction txn = walletService.deposit(deposit, username);

	    TransactionApprovalRequest approveRequest = new TransactionApprovalRequest();
	    approveRequest.setTransactionId(txn.getId());
	    approveRequest.setStatus("APPROVED");

	    Assertions.assertThrows(WalletOperationException.class, () -> {
	        walletService.approve(approveRequest, username); 
	    });
	}

	
}

package com.ing.walletapi.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ing.walletapi.dto.RegisterRequest;
import com.ing.walletapi.dto.TransactionRequest;
import com.ing.walletapi.dto.WalletRequest;
import com.ing.walletapi.entity.OppositePartyType;
import com.ing.walletapi.entity.Role;
import com.ing.walletapi.entity.Wallet;
import com.ing.walletapi.exception.WalletOperationException;

@SpringBootTest
class TransactionServiceTest {

	@Autowired
	private WalletService walletService;

	@Autowired
	private AuthService authService;

	@Test
	void testWithdrawInsufficient() {
		com.ing.walletapi.dto.RegisterRequest reg = new com.ing.walletapi.dto.RegisterRequest();
		reg.setUsername("user2");
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("Test");
		reg.setSurname("User");
		reg.setTckn("1234");
		authService.register(reg);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setWalletName("Main");
		walletRequest.setCurrency("TRY");
		walletRequest.setActiveForShopping(true);
		walletRequest.setActiveForWithdraw(true);
		Wallet wallet = walletService.createWallet(walletRequest, "user2");

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("500"));
		deposit.setOppositePartyType(OppositePartyType.PAYMENT);
		deposit.setOppositeParty("123");
		walletService.deposit(deposit, "user2");

		TransactionRequest withdrawRequest = new TransactionRequest();
		withdrawRequest.setWalletId(wallet.getId());
		withdrawRequest.setAmount(new BigDecimal("600"));
		withdrawRequest.setOppositePartyType(OppositePartyType.PAYMENT);
		withdrawRequest.setOppositeParty("123");

		Assertions.assertThrows(RuntimeException.class, () -> {
			walletService.withdraw(withdrawRequest, "user2");
		});
	}

	@Test
	void testWithdrawExactBalance() {
		RegisterRequest reg = new RegisterRequest();
		reg.setUsername("userExact");
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("Exact");
		reg.setSurname("Balance");
		reg.setTckn("5678");
		authService.register(reg);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setWalletName("Exact Wallet");
		walletRequest.setCurrency("TRY");
		walletRequest.setActiveForShopping(true);
		walletRequest.setActiveForWithdraw(true);
		Wallet wallet = walletService.createWallet(walletRequest, "userExact");

		TransactionRequest deposit = new TransactionRequest();
		deposit.setWalletId(wallet.getId());
		deposit.setAmount(new BigDecimal("500"));
		deposit.setOppositePartyType(OppositePartyType.PAYMENT);
		deposit.setOppositeParty("TR123");
		walletService.deposit(deposit, "userExact");

		TransactionRequest withdraw = new TransactionRequest();
		withdraw.setWalletId(wallet.getId());
		withdraw.setAmount(new BigDecimal("500"));
		withdraw.setOppositePartyType(OppositePartyType.PAYMENT);
		withdraw.setOppositeParty("TR123");

		Assertions.assertDoesNotThrow(() -> {
			walletService.withdraw(withdraw, "userExact");
		});
	}

	@Test
	void testWithdrawNegativeAmount() {
		RegisterRequest reg = new RegisterRequest();
		reg.setUsername("userNeg");
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("Neg");
		reg.setSurname("Ative");
		reg.setTckn("9999");
		authService.register(reg);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setWalletName("Neg Wallet");
		walletRequest.setCurrency("TRY");
		walletRequest.setActiveForShopping(true);
		walletRequest.setActiveForWithdraw(true);
		Wallet wallet = walletService.createWallet(walletRequest, "userNeg");

		TransactionRequest withdraw = new TransactionRequest();
		withdraw.setWalletId(wallet.getId());
		withdraw.setAmount(new BigDecimal("-100"));
		withdraw.setOppositePartyType(OppositePartyType.PAYMENT);
		withdraw.setOppositeParty("TR000");

		Assertions.assertThrows(WalletOperationException.class, () -> {
			walletService.withdraw(withdraw, "userNeg");
		});
	}

	@Test
	void testWithdrawWhenInactiveForWithdraw() {
		RegisterRequest reg = new RegisterRequest();
		reg.setUsername("userInactive");
		reg.setPassword("pass");
		reg.setRole(Role.CUSTOMER);
		reg.setName("In");
		reg.setSurname("Active");
		reg.setTckn("5555");
		authService.register(reg);

		WalletRequest walletRequest = new WalletRequest();
		walletRequest.setWalletName("Inactive Wallet");
		walletRequest.setCurrency("TRY");
		walletRequest.setActiveForShopping(true);
		walletRequest.setActiveForWithdraw(false);
		Wallet wallet = walletService.createWallet(walletRequest, "userInactive");

		TransactionRequest withdraw = new TransactionRequest();
		withdraw.setWalletId(wallet.getId());
		withdraw.setAmount(new BigDecimal("100"));
		withdraw.setOppositePartyType(OppositePartyType.PAYMENT);
		withdraw.setOppositeParty("TR999");

		Assertions.assertThrows(RuntimeException.class, () -> {
			walletService.withdraw(withdraw, "userInactive");
		});
	}

}

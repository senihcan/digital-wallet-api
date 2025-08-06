package com.ing.walletapi.controller;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ing.walletapi.dto.TransactionRequest;
import com.ing.walletapi.dto.WalletRequest;
import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.entity.Wallet;
import com.ing.walletapi.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

	private final WalletService walletService;

	@Operation(summary = "Create a new wallet", description = "Creates a wallet for the authenticated customer user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Wallet created successfully", content = @Content(schema = @Schema(implementation = Wallet.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input or unauthorized user", content = @Content)
	})
	@PostMapping
	public ResponseEntity<Wallet> createWallet(
			@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Wallet creation payload",
					required = true,
					content = @Content(
							schema = @Schema(implementation = WalletRequest.class),
							examples = @ExampleObject(value = """
								{
								  "walletName": "My Wallet",
								  "currency": "TRY",
								  "activeForShopping": true,
								  "activeForWithdraw": true
								}
							""")
					)
			) WalletRequest request,
			@Parameter(hidden = true) Authentication authentication) {
		String username = authentication.getName();
		return ResponseEntity.ok(walletService.createWallet(request, username));
	}

	@Operation(summary = "List wallets", description = "Returns wallets for the authenticated user with optional search filters.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Wallets listed successfully", content = @Content(schema = @Schema(implementation = Page.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping
	public ResponseEntity<Page<Wallet>> listWallets(
			@RequestParam(required = false) String currency,
			@RequestParam(required = false) String walletName,
			@RequestParam(required = false) Boolean activeForShopping,
			@RequestParam(required = false) Boolean activeForWithdraw,
			@RequestParam(required = false) BigDecimal balance,
			@Parameter(hidden = true) Pageable pageable,
			@Parameter(hidden = true) Authentication authentication) {

		Wallet request = new Wallet();
		request.setCurrency(currency);
		request.setWalletName(walletName);
		request.setActiveForShopping(activeForShopping);
		request.setActiveForWithdraw(activeForWithdraw);
		request.setBalance(balance);

		String username = authentication.getName();
		return ResponseEntity.ok(walletService.listWallets(username, request, pageable));
	}

	@Operation(summary = "Deposit money", description = "Deposits an amount into the specified wallet.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Deposit transaction created", content = @Content(schema = @Schema(implementation = Transaction.class))),
			@ApiResponse(responseCode = "400", description = "Invalid transaction request", content = @Content)
	})
	@PostMapping("/deposit")
	public ResponseEntity<Transaction> deposit(
			@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Deposit request body",
					required = true,
					content = @Content(
							schema = @Schema(implementation = TransactionRequest.class),
							examples = @ExampleObject(value = """
								{
								  "walletId": 1,
								  "amount": 1500.00,
								  "oppositePartyType": "IBAN",
								  "oppositeParty": "TR000000000000000000000000"
								}
							""")
					)
			) TransactionRequest request,
			@Parameter(hidden = true) Authentication authentication) {
		String username = authentication.getName();
		return ResponseEntity.ok(walletService.deposit(request, username));
	}

	@Operation(summary = "Withdraw money", description = "Withdraws an amount from the specified wallet.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Withdrawal transaction created", content = @Content(schema = @Schema(implementation = Transaction.class))),
			@ApiResponse(responseCode = "400", description = "Invalid withdrawal or insufficient balance", content = @Content)
	})
	@PostMapping("/withdraw")
	public ResponseEntity<Transaction> withdraw(
			@RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Withdraw request body",
					required = true,
					content = @Content(
							schema = @Schema(implementation = TransactionRequest.class),
							examples = @ExampleObject(value = """
								{
								  "walletId": 1,
								  "amount": 300.00,
								  "oppositePartyType": "IBAN",
								  "oppositeParty": "TR000000000000000000000000"
								}
							""")
					)
			) TransactionRequest request,
			@Parameter(hidden = true) Authentication authentication) {
		String username = authentication.getName();
		return ResponseEntity.ok(walletService.withdraw(request, username));
	}

	@Operation(summary = "Get wallet transactions", description = "Lists all transactions for a specific wallet.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(schema = @Schema(implementation = Transaction.class))),
			@ApiResponse(responseCode = "403", description = "Unauthorized access to wallet", content = @Content)
	})
	@GetMapping("/{walletId}/transactions")
	public ResponseEntity<List<Transaction>> transactions(
			@PathVariable Long walletId,
			@Parameter(hidden = true) Authentication authentication) {
		String username = authentication.getName();
		return ResponseEntity.ok(walletService.listTransactions(walletId, username));
	}
}

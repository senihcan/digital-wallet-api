package com.ing.walletapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ing.walletapi.dto.TransactionApprovalRequest;
import com.ing.walletapi.entity.Transaction;
import com.ing.walletapi.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

	private final WalletService walletService;

	@Operation(
			summary = "Approve or Deny a Transaction",
			description = """
				Allows EMPLOYEE role users to approve or deny a transaction request.
				\n\nTransactionStatus can be:
				- PENDING
				- APPROVED
				- DENIED
			"""
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transaction approved/denied successfully", content = @Content(schema = @Schema(implementation = Transaction.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request or already approved", content = @Content)
	})
	@PostMapping("/approve")
	public ResponseEntity<Transaction> approve(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Approval or denial of a transaction",
					required = true,
					content = @Content(
							schema = @Schema(implementation = TransactionApprovalRequest.class),
							examples = @ExampleObject(value = """
									{
										"transactionId": 1,
										"status": "APPROVED"
									}
									""")
					)
			)
			@RequestBody TransactionApprovalRequest request,
			@Parameter(hidden = true) Authentication authentication) {
		String username = authentication.getName();
		return ResponseEntity.ok(walletService.approve(request, username));
	}

	@Operation(
			summary = "List Pending Transactions",
			description = """
				Returns transactions that are pending approval. Only accessible by EMPLOYEE role.
				\n\nReturned TransactionStatus will be PENDING.
			"""
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pending transactions retrieved", content = @Content(schema = @Schema(implementation = Transaction.class))),
			@ApiResponse(responseCode = "403", description = "Access denied if not EMPLOYEE", content = @Content)
	})
	@GetMapping("/pending")
	public ResponseEntity<List<Transaction>> listPendingTransactions(
			@Parameter(hidden = true) Authentication authentication) {
		return ResponseEntity.ok(walletService.listPendingTransactionsForApproval(authentication.getName()));
	}
}

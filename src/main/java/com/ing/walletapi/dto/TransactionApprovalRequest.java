package com.ing.walletapi.dto;

import lombok.Data;

@Data
public class TransactionApprovalRequest {
    private Long transactionId;
    private String status; // APPROVED or DENIED
}

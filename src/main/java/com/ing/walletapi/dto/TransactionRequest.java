package com.ing.walletapi.dto;

import lombok.Data;

import java.math.BigDecimal;

import com.ing.walletapi.entity.OppositePartyType;

@Data
public class TransactionRequest {
    private BigDecimal amount;
    private Long walletId;
    private OppositePartyType oppositePartyType;
    private String oppositeParty;
}

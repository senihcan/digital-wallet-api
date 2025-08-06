package com.ing.walletapi.dto;

import lombok.Data;

@Data
public class WalletRequest {
    private String walletName;
    private String currency;
    private Boolean activeForShopping;
    private Boolean activeForWithdraw;
}

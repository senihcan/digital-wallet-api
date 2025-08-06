package com.ing.walletapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String walletName;

    private String currency;

    private Boolean activeForShopping;

    private Boolean activeForWithdraw;

    private BigDecimal balance;

    private BigDecimal usableBalance;
}

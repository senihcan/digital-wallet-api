package com.ing.walletapi.specification;

import com.ing.walletapi.entity.Customer;
import com.ing.walletapi.entity.Wallet;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class WalletSpecification {

    public static Specification<Wallet> hasCustomer(Customer customer) {
        return (root, query, criteriaBuilder) ->
                customer == null ? null : criteriaBuilder.equal(root.get("customer"), customer);
    }

    public static Specification<Wallet> hasCurrency(String currency) {
        return (root, query, criteriaBuilder) ->
                currency == null ? null : criteriaBuilder.equal(root.get("currency"), currency);
    }

    public static Specification<Wallet> hasWalletName(String walletName) {
        return (root, query, criteriaBuilder) ->
                walletName == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("walletName")), "%" + walletName.toLowerCase() + "%");
    }

    public static Specification<Wallet> isActiveForShopping(Boolean active) {
        return (root, query, criteriaBuilder) ->
                active == null ? null : criteriaBuilder.equal(root.get("activeForShopping"), active);
    }

    public static Specification<Wallet> isActiveForWithdraw(Boolean active) {
        return (root, query, criteriaBuilder) ->
                active == null ? null : criteriaBuilder.equal(root.get("activeForWithdraw"), active);
    }

    public static Specification<Wallet> equalBalance(BigDecimal balance) {
        return (root, query, criteriaBuilder) ->
                balance == null ? null : criteriaBuilder.equal(root.get("balance"), balance);
    }

  
    

    public static Specification<Wallet> buildSpecification(
            Customer customer,
            String currency,
            String walletName,
            Boolean activeForShopping,
            Boolean activeForWithdraw,
            BigDecimal balance
            
    ) {
        return Specification.where(hasCustomer(customer))
                .and(hasCurrency(currency))
                .and(hasWalletName(walletName))
                .and(isActiveForShopping(activeForShopping))
                .and(isActiveForWithdraw(activeForWithdraw))
                .and(equalBalance(balance));
    }
}
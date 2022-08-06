package com.bigos.payment.adapters.wallet.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallets")
@Entity
public class WalletEntity {

    @Id
    private UUID id;

    private UUID customerId;

    private BigDecimal amount;
}

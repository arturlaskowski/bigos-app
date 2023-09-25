package com.bigos.payment.entities;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.payment.domain.core.Wallet;
import com.bigos.payment.domain.core.WalletId;
import org.springframework.stereotype.Component;

@Component
public class WalletEntityMapper {

    public Wallet walletEntityToWallet(WalletEntity walletEntity) {
        return new Wallet(new WalletId(walletEntity.getId()),
                new CustomerId(walletEntity.getCustomerId()),
                new Money(walletEntity.getAmount()));
    }

    public WalletEntity walletToWalletEntity(Wallet wallet) {
        return new WalletEntity(wallet.getId().id(),
                wallet.getCustomerId().id(),
                wallet.getAmount().amount());
    }
}
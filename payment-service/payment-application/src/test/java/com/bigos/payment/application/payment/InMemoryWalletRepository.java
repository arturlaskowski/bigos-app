package com.bigos.payment.application.payment;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.payment.application.payment.exception.WalletNotFoundException;
import com.bigos.payment.domain.core.Wallet;
import com.bigos.payment.domain.core.WalletId;
import com.bigos.payment.domain.port.WalletRepository;

import java.util.HashMap;
import java.util.Map;

class InMemoryWalletRepository implements WalletRepository {

    private final Map<WalletId, Wallet> store = new HashMap<>();

    @Override
    public Wallet save(Wallet wallet) {
        store.put(wallet.getId(), wallet);
        return wallet;
    }

    @Override
    public Wallet getByCustomerId(CustomerId customerId) {
        return store.values().stream()
                .filter(wallet -> customerId.equals(wallet.getCustomerId()))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("Could not find wallet with customerId: " + customerId.id()));
    }

    void deleteAll() {
        store.clear();
    }
}

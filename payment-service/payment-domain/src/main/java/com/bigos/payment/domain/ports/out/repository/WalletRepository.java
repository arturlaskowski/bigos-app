package com.bigos.payment.domain.ports.out.repository;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.payment.domain.model.Wallet;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Wallet getByCustomerId(CustomerId customerId);
}

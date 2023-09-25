package com.bigos.payment.domain.port;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.payment.domain.core.Wallet;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Wallet getByCustomerId(CustomerId customerId);
}

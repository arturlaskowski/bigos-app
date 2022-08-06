package com.bigos.payment.domain.model;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.payment.domain.model.vo.WalletId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Wallet {

    private WalletId id;

    private CustomerId customerId;

    private Money amount;

    public void addCreditAmount(Money addedAmount) {
        this.amount = this.amount.add(addedAmount);
    }

    public void subtractCreditAmount(Money subtractedAmount) {
        this.amount = this.amount.subtract(subtractedAmount);
    }
}

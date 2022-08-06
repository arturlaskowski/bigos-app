package com.bigos.payment.adapters.payment.mapper;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.vo.WalletId;
import com.bigos.payment.domain.ports.dto.MakePaymentCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessageMapper {

    public Payment completePaymentCommandToPayment(MakePaymentCommand makePaymentCommand, WalletId walletId) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(makePaymentCommand.getOrderId())))
                .walletId(walletId)
                .price(new Money(makePaymentCommand.getPrice()))
                .build();
    }
}

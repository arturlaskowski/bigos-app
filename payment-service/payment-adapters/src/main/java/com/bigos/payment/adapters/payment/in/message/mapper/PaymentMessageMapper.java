package com.bigos.payment.adapters.payment.in.message.mapper;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.ports.dto.payment.MakePaymentCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessageMapper {

    public Payment completePaymentCommandToPayment(MakePaymentCommand makePaymentCommand) {
        return Payment.builder()
                .orderId(new OrderId(UUID.fromString(makePaymentCommand.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(makePaymentCommand.getCustomerId())))
                .price(new Money(makePaymentCommand.getPrice()))
                .build();
    }
}

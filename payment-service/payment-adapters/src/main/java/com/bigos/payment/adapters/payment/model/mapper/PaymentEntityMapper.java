package com.bigos.payment.adapters.payment.model.mapper;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.adapters.payment.model.entity.PaymentEntity;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.vo.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().id())
                .customerId(payment.getCustomerId().id())
                .orderId(payment.getOrderId().id())
                .price(payment.getPrice().amount())
                .status(payment.getStatus())
                .creationDate(payment.getCreationDate())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .creationDate(paymentEntity.getCreationDate())
                .status(paymentEntity.getStatus())
                .build();
    }
}

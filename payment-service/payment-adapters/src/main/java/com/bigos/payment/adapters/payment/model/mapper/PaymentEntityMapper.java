package com.bigos.payment.adapters.payment.model.mapper;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.adapters.payment.model.entity.PaymentEntity;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.vo.PaymentId;
import com.bigos.payment.domain.model.vo.WalletId;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().id())
                .walletId(payment.getWalletId().id())
                .orderId(payment.getOrderId().id())
                .price(payment.getPrice().amount())
                .status(payment.getStatus())
                .createdDate(payment.getCreatedDate())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .id(new PaymentId(paymentEntity.getId()))
                .walletId(new WalletId(paymentEntity.getWalletId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdDate(paymentEntity.getCreatedDate())
                .status(paymentEntity.getStatus())
                .build();
    }
}

package com.bigos.payment.domain.ports.out.repository;


import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.model.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getByOrderId(OrderId orderId);
}

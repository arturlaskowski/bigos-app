package com.bigos.payment.domain.port;


import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.core.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment getByOrderId(OrderId orderId);
}

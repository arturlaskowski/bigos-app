package com.bigos.payment.application.payment;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.application.payment.exception.PaymentNotFoundException;
import com.bigos.payment.domain.core.Payment;
import com.bigos.payment.domain.core.PaymentId;
import com.bigos.payment.domain.port.PaymentRepository;

import java.util.HashMap;
import java.util.Map;

class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<PaymentId, Payment> store = new HashMap<>();

    @Override
    public Payment save(Payment payment) {
        store.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Payment getByOrderId(OrderId orderId) {
        return store.values().stream()
                .filter(payment -> orderId.equals(payment.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new PaymentNotFoundException("Could not find payment with orderId: " + orderId.id()));
    }

    void deleteAll() {
        store.clear();
    }
}

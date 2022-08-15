package com.bigos.order.domain.ports.in.message;

import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;

public interface PaymentStatusEventListener {

    void paymentCompleted(PaymentStatusEvent paymentStatusEvent);

    void paymentCancelled(PaymentStatusEvent paymentStatusEvent);
}

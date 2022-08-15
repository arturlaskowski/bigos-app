package com.bigos.order.adapters.in.message;

import com.bigos.order.adapters.saga.OrderPaymentSaga;
import com.bigos.order.domain.ports.dto.payment.PaymentStatusEvent;
import com.bigos.order.domain.ports.in.message.PaymentStatusEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class PaymentStatusEventListenerImpl implements PaymentStatusEventListener {

    private final OrderPaymentSaga orderPaymentSaga;

    public PaymentStatusEventListenerImpl(OrderPaymentSaga orderPaymentSaga) {
        this.orderPaymentSaga = orderPaymentSaga;
    }

    @Override
    public void paymentCompleted(PaymentStatusEvent paymentStatusEvent) {
        orderPaymentSaga.process(paymentStatusEvent);
        log.info("Order with id: {} is paid", paymentStatusEvent.orderId());
    }

    @Override
    public void paymentCancelled(PaymentStatusEvent paymentStatusEvent) {
        orderPaymentSaga.rollback(paymentStatusEvent);
        log.info("Order with id: {} is cancelled, failure messages: {}"
                , paymentStatusEvent.orderId(), paymentStatusEvent.failureMessages());
    }
}

package com.bigos.payment.domain.ports.in.message;

import com.bigos.payment.domain.ports.dto.payment.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.payment.MakePaymentCommand;

public interface PaymentMessageListener {

    void makePayment(MakePaymentCommand makePaymentCommand);

    void cancelPayment(CancelPaymentCommand cancelPaymentCommand);
}

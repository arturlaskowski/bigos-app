package com.bigos.payment.domain.ports.in.message;

import com.bigos.payment.domain.ports.dto.CancelPaymentCommand;
import com.bigos.payment.domain.ports.dto.MakePaymentCommand;

public interface PaymentMessageListener {

    void makePayment(MakePaymentCommand makePaymentCommand);

    void cancelPayment(CancelPaymentCommand cancelPaymentCommand);
}

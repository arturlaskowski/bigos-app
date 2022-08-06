package com.bigos.payment.domain.service;


import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;

public interface PaymentDomainService {

    PaymentEvent makePayment(Payment payment, Wallet wallet);

    PaymentEvent cancelPayment(Payment payment, Wallet wallet);
}

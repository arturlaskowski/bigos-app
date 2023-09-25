package com.bigos.payment.domain;

import com.bigos.payment.domain.event.PaymentCancelledEvent;
import com.bigos.payment.domain.event.PaymentCompletedEvent;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.exception.PaymentDomainException;
import com.bigos.payment.domain.core.Payment;
import com.bigos.payment.domain.core.Wallet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentDomainService {

    public PaymentEvent makePayment(Payment payment, Wallet wallet) {
        try {
            payment.initialize();
            payment.validatePaymentPrice();
            validateWalletAmount(payment, wallet);
            payment.complete();
            wallet.subtractCreditAmount(payment.getPrice());
            log.info("Payment completed, order id: {}", payment.getOrderId().id());

            return new PaymentCompletedEvent(payment);

        } catch (PaymentDomainException e) {
            payment.rejected();
            log.info("Payment rejected, order id: {}", payment.getOrderId().id());

            return new PaymentRejectedEvent(payment, e.getMessage());
        }
    }

    public PaymentEvent cancelPayment(Payment payment, Wallet wallet) {
        try {
            payment.validatePaymentPrice();
            payment.cancel();
            wallet.addCreditAmount(payment.getPrice());
            log.info("Payment cancelled, order id: {}", payment.getOrderId().id());
            return new PaymentCancelledEvent(payment);

        } catch (PaymentDomainException e) {
            payment.rejected();
            log.info("Payment cancellation rejected, order id: {}", payment.getOrderId().id());
            return new PaymentRejectedEvent(payment, e.getMessage());
        }
    }

    private void validateWalletAmount(Payment payment, Wallet wallet) {
        if (payment.getPrice().isGreaterOrEqualThan(wallet.getAmount())) {
            throw new PaymentDomainException("Payment price: " + payment.getPrice().amount() + " must be greater or equal to wallet amount: " + wallet.getAmount().amount());
        }
    }
}

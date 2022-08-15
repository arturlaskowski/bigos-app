package com.bigos.payment.domain.service;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.event.PaymentCancelledEvent;
import com.bigos.payment.domain.event.PaymentCompletedEvent;
import com.bigos.payment.domain.event.PaymentEvent;
import com.bigos.payment.domain.event.PaymentRejectedEvent;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.model.vo.WalletId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDomainServiceTest {

    private final PaymentDomainService paymentDomainService = new PaymentDomainServiceImpl();

    @Test
    void shouldCompletePayment() {
        //given
        Payment payment = aPayment(new BigDecimal("50.65"));
        Wallet wallet = aWallet(new BigDecimal("76.22"));
        //when
        PaymentEvent paymentEvent = paymentDomainService.makePayment(payment, wallet);
        //then
        assertNotNull(payment.getId());
        assertNotNull(payment.getCreationDate());
        assertTrue(payment.isCompleted());
        assertEquals(new Money(new BigDecimal("25.57")), wallet.getAmount());// 76.22 - 50.65 = 25.57
        assertTrue(paymentEvent instanceof PaymentCompletedEvent);
    }

    @Test
    void shouldRejectCompletePaymentWhenPaymentPriceIsLowerThanZero() {
        //given
        Payment payment = aPayment(new BigDecimal("-9.00"));
        Wallet wallet = aWallet(new BigDecimal("76.22"));
        //when
        PaymentEvent paymentEvent = paymentDomainService.makePayment(payment, wallet);
        //then
        assertNotNull(payment.getId());
        assertNotNull(payment.getCreationDate());
        assertTrue(payment.isRejected());
        assertEquals(new Money(new BigDecimal("76.22")), wallet.getAmount());// the same
        assertTrue(paymentEvent instanceof PaymentRejectedEvent);
        assertEquals("Payment price must be greater than zero. Payment price: -9.00", ((PaymentRejectedEvent) paymentEvent).getFailureMessage());
    }

    @Test
    void shouldRejectCompletePaymentWhenPaymentPriceIsBiggerThanWalletAmount() {
        //given
        Payment payment = aPayment(new BigDecimal("86.22"));
        Wallet wallet = aWallet(new BigDecimal("83.92"));
        //when
        PaymentEvent paymentEvent = paymentDomainService.makePayment(payment, wallet);
        //then
        assertNotNull(payment.getId());
        assertNotNull(payment.getCreationDate());
        assertTrue(payment.isRejected());
        assertEquals(new Money(new BigDecimal("83.92")), wallet.getAmount());// the same
        assertTrue(paymentEvent instanceof PaymentRejectedEvent);
        assertEquals("Payment price: 86.22 must be greater or equal to wallet amount: 83.92", ((PaymentRejectedEvent) paymentEvent).getFailureMessage());
    }

    @Test
    void shouldCancelPayment() {
        //given
        Payment payment = aPayment(new BigDecimal("32.11"));
        Wallet wallet = aWallet(new BigDecimal("50.00"));
        //when
        PaymentEvent paymentEvent = paymentDomainService.cancelPayment(payment, wallet);
        //then
        assertTrue(payment.isCanceled());
        assertEquals(new Money(new BigDecimal("82.11")), wallet.getAmount());// 50.00 + 32.11 = 82.11
        assertTrue(paymentEvent instanceof PaymentCancelledEvent);
    }

    @Test
    void shouldRejectCancelPaymentWhenPaymentPriceIsLowerThanZero() {
        //given
        Payment payment = aPayment(new BigDecimal("-9.00"));
        Wallet wallet = aWallet(new BigDecimal("76.22"));
        //when
        PaymentEvent paymentEvent = paymentDomainService.cancelPayment(payment, wallet);
        //then
        assertTrue(payment.isRejected());
        assertEquals(new Money(new BigDecimal("76.22")), wallet.getAmount());// the same
        assertTrue(paymentEvent instanceof PaymentRejectedEvent);
        assertEquals("Payment price must be greater than zero. Payment price: -9.00", ((PaymentRejectedEvent) paymentEvent).getFailureMessage());
    }

    private Payment aPayment(BigDecimal paymentPrice) {
        return Payment.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .price(new Money(paymentPrice))
                .build();
    }

    private Wallet aWallet(BigDecimal walletAmount) {
        return new Wallet(new WalletId(UUID.randomUUID()), new CustomerId(UUID.randomUUID()), new Money(walletAmount));
    }
}

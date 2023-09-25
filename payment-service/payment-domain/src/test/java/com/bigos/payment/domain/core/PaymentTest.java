package com.bigos.payment.domain.core;

import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.domain.exception.PaymentDomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentTest {

    @Test
    void canInitializePayment() {
        //given
        Payment payment = aPayment();

        //when
        payment.initialize();

        //then
        assertNotNull(payment.getId());
        assertNotNull(payment.getCreationDate());
    }

    @Test
    void canCompletePayment() {
        //given
        Payment payment = aPayment();

        //when
        payment.complete();

        //then
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }


    @Test
    void cannotCompletePaymentWhenPaymentIsInWrongState() {
        //given
        Payment completedPayment = aPayment(PaymentStatus.COMPLETED);
        Payment cancelledPayment = aPayment(PaymentStatus.COMPLETED);

        //expect
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(completedPayment::complete);
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(cancelledPayment::complete);
    }

    @Test
    void canRejectedPayment() {
        //given
        Payment payment = aPayment();

        //when
        payment.rejected();

        //then
        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
    }

    @Test
    void canCancelledPayment() {
        //given
        Payment payment = aPayment();

        //when
        payment.cancel();

        //then
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
    }

    @Test
    void cannotCancelledPaymentWhenPaymentIsInWrongState() {
        //given
        Payment cancelledPayment = aPayment(PaymentStatus.CANCELLED);

        //expect
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(cancelledPayment::cancel);
    }

    private Payment aPayment(PaymentStatus status) {
        return Payment.builder().price(new Money(new BigDecimal("87.73"))).status(status).build();
    }

    private Payment aPayment() {
        return aPayment(new BigDecimal("87.73"));
    }

    private Payment aPayment(BigDecimal paymentPrice) {
        return Payment.builder().price(new Money(paymentPrice)).build();
    }
}
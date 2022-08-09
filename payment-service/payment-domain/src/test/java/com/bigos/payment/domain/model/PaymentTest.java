package com.bigos.payment.domain.model;

import com.bigos.common.domain.vo.Money;
import com.bigos.payment.domain.model.vo.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        assertNotNull(payment.getCreatedDate());
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

    private Payment aPayment() {
        return aPayment(new BigDecimal("87.73"));
    }

    private Payment aPayment(BigDecimal paymentPrice) {
        return Payment.builder()
                .price(new Money(paymentPrice))
                .build();
    }
}
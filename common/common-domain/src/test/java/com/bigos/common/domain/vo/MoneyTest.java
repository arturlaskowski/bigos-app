package com.bigos.common.domain.vo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void canCreateMoneyFromBigDecimal() {
        //expect
        assertEquals(new BigDecimal("10.00"), new Money(new BigDecimal("10.00")).amount());
        assertEquals(new BigDecimal("17.89"), new Money(new BigDecimal("17.89")).amount());
        assertEquals(new BigDecimal("0.00"), new Money(new BigDecimal("0.00")).amount());
    }

    @Test
    void shouldSetScale() {
        //expect
        assertEquals(new BigDecimal("10.01"), new Money(new BigDecimal("10.006")).amount());
        assertEquals(new BigDecimal("10.00"), new Money(new BigDecimal("10.004")).amount());
        assertEquals(new BigDecimal("10.00"), new Money(new BigDecimal("10.005")).amount());
    }

    @Test
    void canAddMoney() {
        //expect
        assertEquals(new Money(new BigDecimal("10.00")), new Money(new BigDecimal("3.00")).add(new Money(new BigDecimal("7.00"))));
        assertEquals(new Money(new BigDecimal("35.41")), new Money(new BigDecimal("18.94")).add(new Money(new BigDecimal("16.47"))));
        assertEquals(new Money(new BigDecimal("7.00")), new Money(new BigDecimal("0.00")).add(new Money(new BigDecimal("7.00"))));
        assertEquals(new Money(BigDecimal.valueOf(-40.00)), new Money(BigDecimal.valueOf(-52.00)).add(new Money(new BigDecimal("12.00"))));
    }

    @Test
    void canSubtractMoney() {
        //expect
        assertEquals(new Money(new BigDecimal("30.00")), new Money(new BigDecimal("50.00")).subtract(new Money(new BigDecimal("20.00"))));
        assertEquals(Money.ZERO, new Money(new BigDecimal("50.00")).subtract(new Money(new BigDecimal("50.00"))));
        assertEquals(new Money(new BigDecimal("27.04")), new Money(new BigDecimal("56.36")).subtract(new Money(new BigDecimal("29.32"))));
        assertEquals(new Money(BigDecimal.valueOf(-4.00)), new Money(new BigDecimal("31.00")).subtract(new Money(new BigDecimal("35.00"))));
    }

    @Test
    void canMultiplyMoney() {
        //expect
        assertEquals(new Money(new BigDecimal("9.00")), new Money(new BigDecimal("3.00")).multiply(3));
        assertEquals(new Money(new BigDecimal("8.65")), new Money(new BigDecimal("8.65")).multiply(1));
        assertEquals(new Money(new BigDecimal("65.28")), new Money(new BigDecimal("16.32")).multiply(4));
        assertEquals(Money.ZERO, new Money(new BigDecimal("72.91")).multiply(0));
    }

    @Test
    void isGreaterThanZero() {
        //expect
        assertTrue(new Money(new BigDecimal("0.01")).isGreaterThanZero());
        assertTrue(new Money(new BigDecimal("40.00")).isGreaterThanZero());

        assertFalse(Money.ZERO.isGreaterThanZero());
        assertFalse(new Money(BigDecimal.valueOf(-0.01)).isGreaterThanZero());
    }

    @Test
    void isGreaterOrEqualThan() {
        //expect
        assertTrue(new Money(new BigDecimal("87.15")).isGreaterOrEqualThan(new Money(new BigDecimal("56.01"))));
        assertTrue(new Money(new BigDecimal("56.02")).isGreaterOrEqualThan(new Money(new BigDecimal("56.01"))));
        assertTrue(new Money(new BigDecimal("56.01")).isGreaterOrEqualThan(new Money(new BigDecimal("56.01"))));

        assertFalse(new Money(new BigDecimal("15.87")).isGreaterOrEqualThan(new Money(new BigDecimal("83.91"))));
        assertFalse(new Money(new BigDecimal("76.01")).isGreaterOrEqualThan(new Money(new BigDecimal("76.02"))));
    }
}
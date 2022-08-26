package com.bigos.payment.domain.ports.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CancelPaymentCommand {

    private String id;

    private String sagaId;

    private String orderId;

    private String customerId;

    private BigDecimal price;
}

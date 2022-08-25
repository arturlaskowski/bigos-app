package com.bigos.payment.domain.ports.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CancelPaymentCommand {

    @NotNull
    private String id;

    @NotNull
    private String sagaId;

    @NotNull
    private String orderId;

    @NotNull
    private String customerId;

    @NotNull
    private BigDecimal price;
}

package com.bigos.order.domain.ports.dto.get;

import com.bigos.order.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record GetOrderResponse(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull UUID restaurantId,
        @NotNull OrderStatus status,
        @NotNull BigDecimal price,
        @NotNull List<BasketItemGetDto> items,
        @NotNull OrderAddressGetDto address
) {


}

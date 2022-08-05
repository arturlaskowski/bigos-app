package com.bigos.order.domain.ports.dto.get;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record GetOrderQuery(@NotNull UUID orderId) {
}

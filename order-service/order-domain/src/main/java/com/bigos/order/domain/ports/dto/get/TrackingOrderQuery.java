package com.bigos.order.domain.ports.dto.get;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record TrackingOrderQuery(@NotNull UUID orderId) {
}

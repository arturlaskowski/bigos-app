package com.bigos.order.domain.ports.dto.get;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record OrderAddressGetDto(
        @NotNull UUID id,
        @NotNull String street,
        @NotNull String postalCode,
        @NotNull String city,
        String houseNo
) {
}

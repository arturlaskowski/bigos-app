package com.bigos.order.domain.ports.dto.order.create;

import javax.validation.constraints.NotNull;

public record OrderAddressCreateDto(
        @NotNull String street,
        @NotNull String postalCode,
        @NotNull String city,
        String houseNo
) {
}
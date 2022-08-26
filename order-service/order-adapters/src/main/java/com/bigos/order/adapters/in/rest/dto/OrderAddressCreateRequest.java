package com.bigos.order.adapters.in.rest.dto;

import javax.validation.constraints.NotNull;

public record OrderAddressCreateRequest(
        @NotNull String street,
        @NotNull String postalCode,
        @NotNull String city,
        String houseNo
) {
}

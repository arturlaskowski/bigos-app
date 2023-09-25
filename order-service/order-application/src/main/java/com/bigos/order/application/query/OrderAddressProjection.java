package com.bigos.order.application.query;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderAddressProjection(
        UUID id,
        String street,
        String postalCode,
        String city,
        String houseNo
) {
}

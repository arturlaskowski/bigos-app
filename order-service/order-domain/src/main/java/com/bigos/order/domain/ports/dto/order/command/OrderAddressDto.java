package com.bigos.order.domain.ports.dto.order.command;

import lombok.Builder;

@Builder
public record OrderAddressDto(
        String street,
        String postalCode,
        String city,
        String houseNo
) {
}

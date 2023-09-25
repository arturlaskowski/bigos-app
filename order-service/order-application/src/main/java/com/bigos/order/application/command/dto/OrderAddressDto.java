package com.bigos.order.application.command.dto;

import lombok.Builder;

@Builder
public record OrderAddressDto(
        String street,
        String postalCode,
        String city,
        String houseNo
) {
}

package com.bigos.infrastructure.kafka.model;

public record OrderAddressDto(
        String orderAddressId,
        String street,
        String postCode,
        String city,
        String houseNo
) {
}

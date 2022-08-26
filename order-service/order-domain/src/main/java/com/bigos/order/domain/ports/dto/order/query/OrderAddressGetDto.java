package com.bigos.order.domain.ports.dto.order.query;

import java.util.UUID;

public record OrderAddressGetDto(
        UUID id,
        String street,
        String postalCode,
        String city,
        String houseNo
) {
}

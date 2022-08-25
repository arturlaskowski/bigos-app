package com.bigos.infrastructure.kafka.model;

import java.io.Serializable;

public record OrderAddressMessageDto(
        String orderAddressId,
        String street,
        String postCode,
        String city,
        String houseNo
) implements Serializable {
}

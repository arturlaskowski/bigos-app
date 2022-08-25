package com.bigos.order.adapters.outbox.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OrderAddressPayload(
        @JsonProperty String orderAddressId,
        @JsonProperty String street,
        @JsonProperty String postCode,
        @JsonProperty String city,
        @JsonProperty String houseNo
) {
}

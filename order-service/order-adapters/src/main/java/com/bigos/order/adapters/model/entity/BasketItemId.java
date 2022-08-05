package com.bigos.order.adapters.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BasketItemId implements Serializable {

    private Integer itemNumber;

    private UUID orderId;

}

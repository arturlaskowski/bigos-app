package com.bigos.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class OrderAddress {

    private UUID id;

    private String street;

    private String postCode;

    private String city;

    private String houseNo;
}

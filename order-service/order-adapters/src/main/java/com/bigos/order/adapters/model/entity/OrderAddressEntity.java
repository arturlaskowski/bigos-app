package com.bigos.order.adapters.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "order_address")
public class OrderAddressEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String postCode;

    @Column(nullable = false)
    private String city;

    private String houseNo;

    @Column(name = "order_id")
    private UUID orderId;

    @OneToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;
}

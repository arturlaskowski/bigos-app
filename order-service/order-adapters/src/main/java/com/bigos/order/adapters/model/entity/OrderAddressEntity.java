package com.bigos.order.adapters.model.entity;

import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "order_id")
    @Setter
    private OrderEntity order;
}

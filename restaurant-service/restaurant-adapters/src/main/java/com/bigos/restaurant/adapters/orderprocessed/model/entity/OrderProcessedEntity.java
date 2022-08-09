package com.bigos.restaurant.adapters.orderprocessed.model.entity;

import com.bigos.restaurant.domain.model.vo.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "order_processed")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class OrderProcessedEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItemEntity> items;
}

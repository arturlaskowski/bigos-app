package com.bigos.restaurant.adapters.orderprocessed.outbox.model.entity;

import com.bigos.common.adapters.outbox.model.entity.OutboxEntity;
import com.bigos.common.domain.vo.OrderApprovalStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "order_processed_outbox")
@Getter
@Setter
public class OrderProcessedOutboxEntity extends OutboxEntity {

    @Enumerated(EnumType.STRING)
    private OrderApprovalStatus approvalStatus;
}

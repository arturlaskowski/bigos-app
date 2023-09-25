
package com.bigos.restaurant.application.orderprocessed.outbox.dto;

import com.bigos.common.applciaiton.outbox.model.AbstractOutboxMessage;
import com.bigos.common.domain.vo.OrderApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProcessedOutboxMessage extends AbstractOutboxMessage {

    private OrderApprovalStatus approvalStatus;
}



package com.bigos.restaurant.domain.ports.dto.outbox;

import com.bigos.common.domain.outbox.AbstractOutboxMessage;
import com.bigos.common.domain.vo.OrderApprovalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProcessedOutboxMessage extends AbstractOutboxMessage {

    private OrderApprovalStatus approvalStatus;
}


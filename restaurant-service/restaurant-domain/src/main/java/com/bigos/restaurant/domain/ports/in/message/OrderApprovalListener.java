package com.bigos.restaurant.domain.ports.in.message;

import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;

public interface OrderApprovalListener {

    void approveOrder(OrderPaidEvent orderPaidEvent);
}

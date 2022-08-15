package com.bigos.restaurant.domain.ports.in.message;

import com.bigos.restaurant.domain.ports.dto.OrderPaidEvent;

public interface OrderPaidListener {

    void acceptOrder(OrderPaidEvent orderPaidEvent);
}

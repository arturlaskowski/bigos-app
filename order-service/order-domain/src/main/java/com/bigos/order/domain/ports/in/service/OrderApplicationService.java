package com.bigos.order.domain.ports.in.service;

import com.bigos.order.domain.ports.dto.order.command.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.command.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.order.query.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.query.GetOrderResponse;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand);

    GetOrderResponse getOrder(GetOrderQuery getOrderQuery);
}

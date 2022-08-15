package com.bigos.order.domain.ports.in.service;

import com.bigos.order.domain.ports.dto.order.create.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.create.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.order.get.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.get.GetOrderResponse;

import javax.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    GetOrderResponse getOrder(@Valid GetOrderQuery getOrderQuery);
}

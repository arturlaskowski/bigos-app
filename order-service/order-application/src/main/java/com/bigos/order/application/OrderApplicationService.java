package com.bigos.order.application;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.application.command.OrderCreateCommandHandler;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.query.GetOrderQueryHandler;
import com.bigos.order.application.query.OrderProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class OrderApplicationService {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final GetOrderQueryHandler getOrderQueryHandler;

    public OrderApplicationService(OrderCreateCommandHandler orderCreateCommandHandler, GetOrderQueryHandler getOrderQueryHandler) {
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.getOrderQueryHandler = getOrderQueryHandler;
    }

    @Transactional
    public OrderId createOrder(CreateOrderCommand createOrderCommand) {
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    public OrderProjection getOrder(UUID orderId) {
        return getOrderQueryHandler.getOrderById(orderId);
    }
}

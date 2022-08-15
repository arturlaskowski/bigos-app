package com.bigos.order.adapters;

import com.bigos.order.adapters.command.OrderCreateCommandHandler;
import com.bigos.order.adapters.query.GetOrderQueryHandler;
import com.bigos.order.domain.ports.dto.order.create.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.create.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.order.get.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.get.GetOrderResponse;
import com.bigos.order.domain.ports.in.service.OrderApplicationService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final GetOrderQueryHandler getOrderQueryHandler;

    public OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler, GetOrderQueryHandler getOrderQueryHandler) {
        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.getOrderQueryHandler = getOrderQueryHandler;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public GetOrderResponse getOrder(GetOrderQuery getOrderQuery) {
        return getOrderQueryHandler.getOrderById(getOrderQuery);
    }
}

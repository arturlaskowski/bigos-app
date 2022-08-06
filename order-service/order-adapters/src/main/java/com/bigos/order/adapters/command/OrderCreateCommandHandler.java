package com.bigos.order.adapters.command;

import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.ports.dto.create.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.create.CreateOrderResponse;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import com.bigos.order.domain.service.OrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCommandMapper orderMapper;
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;

    public OrderCreateCommandHandler(OrderCommandMapper orderCommandMapper, OrderDomainService orderDomainService, OrderRepository orderRepository) {
        this.orderMapper = orderCommandMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        Order order = orderMapper.createOrderCommandToOrder(createOrderCommand);

        OrderCreatedEvent orderCreatedEvent = orderDomainService.create(order);

        orderRepository.save(order);

        CreateOrderResponse createOrderResponse = orderMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());

        log.info("Order created id: {}", orderCreatedEvent.getOrder().getId().id());
        return createOrderResponse;
    }
}

package com.bigos.order.adapters.command;

import com.bigos.order.adapters.command.mapper.OrderCommandMapper;
import com.bigos.order.adapters.outbox.dto.mapper.OrderOutboxMapper;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.order.command.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.command.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.outbox.OrderCreatedOutboxMessage;
import com.bigos.order.domain.ports.out.repository.OrderOutboxRepository;
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
    private final OrderOutboxMapper orderOutboxMapper;
    private final OrderOutboxRepository outboxRepository;

    public OrderCreateCommandHandler(OrderCommandMapper orderMapper,
                                     OrderDomainService orderDomainService,
                                     OrderRepository orderRepository,
                                     OrderOutboxMapper orderOutboxMapper, OrderOutboxRepository outboxRepository) {
        this.orderMapper = orderMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderOutboxMapper = orderOutboxMapper;
        this.outboxRepository = outboxRepository;
    }


    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        Order order = orderMapper.createOrderCommandToOrder(createOrderCommand);

        OrderCreatedEvent orderCreatedEvent = orderDomainService.create(order);

        orderRepository.save(order);

        CreateOrderResponse createOrderResponse = orderMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder());

        saveOrderCreatedOutboxMessage(orderCreatedEvent);

        log.info("Order created id: {}", orderCreatedEvent.getOrder().getId().id());
        return createOrderResponse;
    }

    private void saveOrderCreatedOutboxMessage(OrderCreatedEvent orderCreatedEvent) {
        OrderCreatedOutboxMessage orderCreatedOutboxMessage =
                orderOutboxMapper.orderCreatedEventToOrderCreatedOutboxMessage(orderCreatedEvent);

        outboxRepository.save(orderCreatedOutboxMessage);
    }

}

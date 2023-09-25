package com.bigos.order.application.command;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.outbox.dto.OrderCreatedOutboxMessage;
import com.bigos.order.application.outbox.dto.mapper.OrderOutboxMapper;
import com.bigos.order.application.outbox.repository.OrderOutboxRepository;
import com.bigos.order.domain.OrderDomainService;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.event.OrderCreatedEvent;
import com.bigos.order.domain.port.OrderRepository;
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

    public OrderCreateCommandHandler(final OrderCommandMapper orderMapper,
                                     final OrderDomainService orderDomainService,
                                     final OrderRepository orderRepository,
                                     final OrderOutboxMapper orderOutboxMapper,
                                     final OrderOutboxRepository outboxRepository) {
        this.orderMapper = orderMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderOutboxMapper = orderOutboxMapper;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public OrderId createOrder(CreateOrderCommand createOrderCommand) {
        Order order = orderMapper.createOrderCommandToOrder(createOrderCommand);

        OrderCreatedEvent orderCreatedEvent = orderDomainService.create(order);
        var orderId = orderCreatedEvent.getOrder().getId();

        orderRepository.save(order);
        saveOrderCreatedOutboxMessage(orderCreatedEvent);

        log.info("Order created id: {}", orderCreatedEvent.getOrder().getId().id());
        return orderId;
    }

    private void saveOrderCreatedOutboxMessage(OrderCreatedEvent orderCreatedEvent) {
        OrderCreatedOutboxMessage orderCreatedOutboxMessage =
                orderOutboxMapper.orderCreatedEventToOrderCreatedOutboxMessage(orderCreatedEvent);

        outboxRepository.save(orderCreatedOutboxMessage);
    }
}

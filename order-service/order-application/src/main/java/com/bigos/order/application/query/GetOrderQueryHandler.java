package com.bigos.order.application.query;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class GetOrderQueryHandler {

    private final OrderQueryRepository orderQueryRepository;

    public GetOrderQueryHandler(final OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

    @Transactional(readOnly = true)
    public OrderProjection getOrderById(UUID orderId) {
        return orderQueryRepository.getOrderProjection(orderId);
    }
}

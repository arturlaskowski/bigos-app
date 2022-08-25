package com.bigos.order.adapters.in.message;

import com.bigos.order.adapters.saga.OrderApprovalSaga;
import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;
import com.bigos.order.domain.ports.in.message.RestaurantApprovalEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class RestaurantApprovalEventListenerImpl implements RestaurantApprovalEventListener {

    private final OrderApprovalSaga orderApprovalSaga;

    public RestaurantApprovalEventListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }

    @Override
    public void orderApproved(RestaurantApprovalEvent restaurantApprovalEvent) {
        orderApprovalSaga.process(restaurantApprovalEvent);
        log.info("Order with id: {} is approved", restaurantApprovalEvent.orderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalEvent restaurantApprovalEvent) {
        orderApprovalSaga.rollback(restaurantApprovalEvent);
        log.info("Order with id: {} is start cancelling, failure messages: {}",
                restaurantApprovalEvent.orderId(), restaurantApprovalEvent.failureMessages());
    }
}

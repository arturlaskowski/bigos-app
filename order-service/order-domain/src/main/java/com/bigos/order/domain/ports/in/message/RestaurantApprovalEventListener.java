package com.bigos.order.domain.ports.in.message;

import com.bigos.order.domain.ports.dto.restaurant.RestaurantApprovalEvent;

public interface RestaurantApprovalEventListener {

    void orderApproved(RestaurantApprovalEvent restaurantApprovalEvent);

    void orderRejected(RestaurantApprovalEvent restaurantApprovalEvent);
}

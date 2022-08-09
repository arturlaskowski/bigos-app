package com.bigos.restaurant.domain.model;

import com.bigos.common.domain.model.AggregateRoot;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.exception.RestaurantDomainException;
import com.bigos.restaurant.domain.model.vo.OrderApprovalStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

import static com.bigos.restaurant.domain.model.vo.OrderApprovalStatus.APPROVED;
import static com.bigos.restaurant.domain.model.vo.OrderApprovalStatus.REJECTED;

@Getter
@Builder
public class OrderProcessed implements AggregateRoot {

    private OrderId id;

    private RestaurantId restaurantId;

    private Money price;

    private List<OrderItem> items;

    private OrderApprovalStatus approvalStatus;

    public void initialize() {
        items.forEach(OrderItem::initialize);
    }

    public void validate() {
        Money totalProductAmount = items.stream().map(product ->
                product.getPrice().multiply(product.getQuantity().numberOfElements())
        ).reduce(Money.ZERO, Money::add);

        if (!totalProductAmount.equals(price)) {
            throw new RestaurantDomainException("Order amount: " + price.amount()
                    + " is different than total products price : " + totalProductAmount.amount());
        }
    }

    public void approve() {
        approvalStatus = APPROVED;
    }

    public void reject() {
        approvalStatus = REJECTED;
    }
}

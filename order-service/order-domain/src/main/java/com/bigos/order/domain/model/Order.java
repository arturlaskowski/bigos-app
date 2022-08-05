package com.bigos.order.domain.model;

import com.bigos.common.domain.model.AggregateRoot;
import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.order.domain.exception.OrderDomainException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class Order implements AggregateRoot {

    private OrderId id;

    private CustomerId customerId;

    private RestaurantId restaurantId;

    private OrderAddress deliveryAddress;

    private Money price;

    private List<BasketItem> basket;

    private OrderStatus status;

    private String failureMessages;

    public void initialize() {
        id = new OrderId(UUID.randomUUID());
        status = OrderStatus.PENDING;
        initializeBasketItems();
    }

    public void validate() {
        validateInitialization();
        validatePrice();
    }

    public boolean isPendingStatus() {
        return OrderStatus.PENDING == status;
    }

    public boolean isPaidStatus() {
        return OrderStatus.PAID == status;
    }

    public boolean isApprovedStatus() {
        return OrderStatus.APPROVED == status;
    }

    public boolean isCancellingStatus() {
        return OrderStatus.CANCELLING == status;
    }

    public boolean isCancelledStatus() {
        return OrderStatus.CANCELLED == status;
    }

    public void pay() {
        if (status != OrderStatus.PENDING) {
            throw new OrderDomainException("The payment operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = OrderStatus.PAID;
    }

    public void approve() {
        if (status != OrderStatus.PAID) {
            throw new OrderDomainException("The approve operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = OrderStatus.APPROVED;
    }

    public void startCancelling(String failureMessage) {
        if (status != OrderStatus.PAID) {
            throw new OrderDomainException("The init cancel operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessage);
    }

    public void cancel() {
        if (status != OrderStatus.CANCELLING) {
            throw new OrderDomainException("The cancel operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = OrderStatus.CANCELLED;
    }

    private void validateInitialization() {
        if (status == null || getId() == null) {
            throw new OrderDomainException("Order is not correctly initialized");
        }
    }

    private void validatePrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Order price must be greater than zero");
        }

        Money basketItemsTotalCost = basket.stream().map(item -> {
            validateBasketItemPrice(item);
            return item.getTotalPrice();
        }).reduce(Money.ZERO, Money::add);

        if (!price.equals(basketItemsTotalCost)) {
            throw new OrderDomainException("Total order price: " + price.amount()
                    + " is different than basket items total: " + basketItemsTotalCost.amount());
        }
    }

    private void validateBasketItemPrice(BasketItem basketItem) {
        if (!basketItem.isValidPrice()) {
            throw new OrderDomainException("Incorrect basket item price: " + basketItem.getTotalPrice().amount());
        }
    }

    private void initializeBasketItems() {
        int itemNumber = 1;
        for (BasketItem item : basket) {
            item.initializeBasketItem(this.id, itemNumber++);
        }
    }

    private void updateFailureMessages(String message) {
        if (message != null) {
            failureMessages = message;
        }
    }

    public List<BasketItem> getBasket() {
        return List.copyOf(basket);
    }
}

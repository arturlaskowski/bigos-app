package com.bigos.order.domain.core;

import com.bigos.common.domain.model.AggregateRoot;
import com.bigos.common.domain.vo.*;
import com.bigos.order.domain.exception.OrderDomainException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.bigos.common.domain.vo.OrderStatus.*;
import static java.time.Instant.now;

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

    private Instant creationDate;

    private List<String> failureMessages;

    public void initialize() {
        id = new OrderId(UUID.randomUUID());
        status = PENDING;
        creationDate = now();
        initializeBasketItems();
    }

    public boolean isPendingStatus() {
        return PENDING == status;
    }

    public boolean isPaidStatus() {
        return PAID == status;
    }

    public boolean isApprovedStatus() {
        return APPROVED == status;
    }

    public boolean isCancellingStatus() {
        return CANCELLING == status;
    }

    public boolean isCancelledStatus() {
        return CANCELLED == status;
    }

    public void pay() {
        if (PENDING != status) {
            throw new OrderDomainException("The payment operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = PAID;
    }

    public void approve() {
        if (PAID != status) {
            throw new OrderDomainException("The approve operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = APPROVED;
    }

    public void startCancelling(String failureMessage) {
        if (PAID != status) {
            throw new OrderDomainException("The init cancel operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = CANCELLING;
        updateFailureMessages(failureMessage);
    }

    public void cancel(String failureMessage) {
        if (!(CANCELLING == status || PENDING == status)) {
            throw new OrderDomainException("The cancel operation cannot be performed. Order is in incorrect state: " + status);
        }
        status = CANCELLED;
        updateFailureMessages(failureMessage);
    }

    public void validatePrice() {
        if (!price.isGreaterThanZero()) {
            throw new OrderDomainException("Order price: " + price.amount() + " must be greater than zero");
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

    public void updateFailureMessages(String message) {
        if (failureMessages == null) {
            failureMessages = new ArrayList<>();
        }
        if (message != null) {
            failureMessages.add(message);
        }
    }

    public List<BasketItem> getBasket() {
        return List.copyOf(basket);
    }
}

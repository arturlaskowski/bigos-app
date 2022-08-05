package com.bigos.order.adapters.query;

import com.bigos.order.domain.model.BasketItem;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.model.OrderAddress;
import com.bigos.order.domain.ports.dto.get.BasketItemGetDto;
import com.bigos.order.domain.ports.dto.get.GetOrderResponse;
import com.bigos.order.domain.ports.dto.get.OrderAddressGetDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderQueryMapper {

    public GetOrderResponse orderToGetOrderResponse(Order order) {
        return GetOrderResponse.builder()
                .orderId(order.getId().id())
                .restaurantId(order.getRestaurantId().id())
                .customerId(order.getCustomerId().id())
                .price(order.getPrice().amount())
                .address(addressToOrderAddressDto(order.getDeliveryAddress()))
                .items(basketItemToBasketItemsDto(order.getBasket()))
                .build();
    }

    private OrderAddressGetDto addressToOrderAddressDto(OrderAddress address) {
        return new OrderAddressGetDto(address.getId(),
                address.getStreet(),
                address.getPostCode(),
                address.getCity(),
                address.getHouseNo());
    }

    private List<BasketItemGetDto> basketItemToBasketItemsDto(List<BasketItem> items) {
        return items.stream()
                .map(item ->
                        new BasketItemGetDto(item.getItemNumber(),
                                item.getProduct().getId().id(),
                                item.getQuantity().numberOfElements(),
                                item.getProduct().getPrice().amount(),
                                item.getTotalPrice().amount()))
                .toList();
    }
}

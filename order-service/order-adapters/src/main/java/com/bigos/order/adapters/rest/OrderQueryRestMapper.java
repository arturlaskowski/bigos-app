package com.bigos.order.adapters.rest;

import com.bigos.order.adapters.rest.dto.GetOrderDetailsResponse;
import com.bigos.order.application.query.BasketItemProjection;
import com.bigos.order.application.query.OrderAddressProjection;
import com.bigos.order.application.query.OrderProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderQueryRestMapper {

    public GetOrderDetailsResponse orderToGetOrderResponse(OrderProjection projection) {
        return GetOrderDetailsResponse.builder()
                .orderId(projection.id())
                .restaurantId(projection.restaurantId())
                .customerId(projection.customerId())
                .price(projection.price())
                .status(projection.status())
                .address(addressToOrderAddressDto(projection.deliveryAddress()))
                .items(basketItemToBasketItemsDto(projection.basket()))
                .failureMessages(projection.failureMessages())
                .build();
    }

    private GetOrderDetailsResponse.OrderAddressDto addressToOrderAddressDto(OrderAddressProjection address) {
        return new GetOrderDetailsResponse.OrderAddressDto(
                address.id(),
                address.street(),
                address.postalCode(),
                address.city(),
                address.houseNo());
    }

    private List<GetOrderDetailsResponse.BasketItemDto> basketItemToBasketItemsDto(List<BasketItemProjection> items) {
        return items.stream()
                .map(item ->
                        new GetOrderDetailsResponse.BasketItemDto(item.itemNumber(),
                                item.productId(),
                                item.quantity(),
                                item.price(),
                                item.totalPrice()))
                .toList();
    }
}

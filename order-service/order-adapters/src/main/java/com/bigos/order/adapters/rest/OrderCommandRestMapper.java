package com.bigos.order.adapters.rest;

import com.bigos.order.adapters.rest.dto.CreateOrderRequest;
import com.bigos.order.application.command.dto.BasketItemDto;
import com.bigos.order.application.command.dto.CreateOrderCommand;
import com.bigos.order.application.command.dto.OrderAddressDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderCommandRestMapper {

    public CreateOrderCommand creteOrderRequestToCreateOrderCommand(CreateOrderRequest createOrderRequest) {
        return CreateOrderCommand.builder()
                .customerId(createOrderRequest.customerId())
                .restaurantId(createOrderRequest.restaurantId())
                .price(createOrderRequest.price())
                .address(orderAddressCreateRequestToOrderAddressDto(createOrderRequest.address()))
                .items(basketItemsCreateRequestToBasketItemsDto(createOrderRequest.items()))
                .build();
    }

    private OrderAddressDto orderAddressCreateRequestToOrderAddressDto(CreateOrderRequest.OrderAddressCreateRequest addressRequest) {
        return OrderAddressDto.builder()
                .street(addressRequest.street())
                .city(addressRequest.city())
                .postalCode(addressRequest.postalCode())
                .houseNo(addressRequest.houseNo())
                .build();
    }

    private List<BasketItemDto> basketItemsCreateRequestToBasketItemsDto(List<CreateOrderRequest.BasketItemCreateRequest> basketItemsRequest) {
        return basketItemsRequest.stream()
                .map(request ->
                        BasketItemDto.builder()
                                .productId(request.productId())
                                .price(request.price())
                                .quantity(request.quantity())
                                .totalPrice(request.totalPrice())
                                .build()
                ).toList();
    }
}

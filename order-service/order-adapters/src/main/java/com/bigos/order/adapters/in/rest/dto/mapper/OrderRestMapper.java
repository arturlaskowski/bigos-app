package com.bigos.order.adapters.in.rest.dto.mapper;

import com.bigos.order.adapters.in.rest.dto.BasketItemCreateRequest;
import com.bigos.order.adapters.in.rest.dto.CreateOrderRequest;
import com.bigos.order.adapters.in.rest.dto.OrderAddressCreateRequest;
import com.bigos.order.domain.ports.dto.order.command.BasketItemDto;
import com.bigos.order.domain.ports.dto.order.command.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.command.OrderAddressDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderRestMapper {

    public CreateOrderCommand creteOrderRequestToCreateOrderCommand(CreateOrderRequest createOrderRequest) {
        return CreateOrderCommand.builder()
                .customerId(createOrderRequest.customerId())
                .restaurantId(createOrderRequest.restaurantId())
                .price(createOrderRequest.price())
                .address(orderAddressCreateRequestToOrderAddressDto(createOrderRequest.address()))
                .items(basketItemsCreateRequestToBasketItemsDto(createOrderRequest.items()))
                .build();
    }

    private OrderAddressDto orderAddressCreateRequestToOrderAddressDto(OrderAddressCreateRequest addressRequest) {
        return OrderAddressDto.builder()
                .street(addressRequest.street())
                .city(addressRequest.city())
                .postalCode(addressRequest.postalCode())
                .houseNo(addressRequest.houseNo())
                .build();
    }

    private List<BasketItemDto> basketItemsCreateRequestToBasketItemsDto(List<BasketItemCreateRequest> basketItemsRequest) {
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

package com.bigos.order.adapters.rest;

import com.bigos.order.adapters.rest.dto.CreateOrderRequest;
import com.bigos.order.adapters.rest.dto.CreateOrderResponse;
import com.bigos.order.adapters.rest.dto.GetOrderDetailsResponse;
import com.bigos.order.application.OrderApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderCommandRestMapper orderCommandRestMapper;
    private final OrderQueryRestMapper orderQueryRestMapper;

    public OrderController(final OrderApplicationService orderApplicationService,
                           final OrderCommandRestMapper orderCommandRestMapper,
                           final OrderQueryRestMapper orderQueryRestMapper) {
        this.orderApplicationService = orderApplicationService;
        this.orderCommandRestMapper = orderCommandRestMapper;
        this.orderQueryRestMapper = orderQueryRestMapper;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(new CreateOrderResponse(
                orderApplicationService.createOrder(orderCommandRestMapper.creteOrderRequestToCreateOrderCommand(createOrderRequest)).id()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GetOrderDetailsResponse> getDetailsOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderQueryRestMapper.orderToGetOrderResponse(orderApplicationService.getOrder(orderId)));
    }
}

package com.bigos.order.adapters.in.rest;

import com.bigos.order.adapters.in.rest.dto.CreateOrderRequest;
import com.bigos.order.adapters.in.rest.dto.mapper.OrderRestMapper;
import com.bigos.order.domain.ports.dto.order.command.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.order.query.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.query.GetOrderResponse;
import com.bigos.order.domain.ports.in.service.OrderApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderRestMapper orderRestMapper;

    public OrderController(OrderApplicationService orderApplicationService, OrderRestMapper orderRestMapper) {
        this.orderApplicationService = orderApplicationService;
        this.orderRestMapper = orderRestMapper;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(orderApplicationService.createOrder(orderRestMapper.creteOrderRequestToCreateOrderCommand(createOrderRequest)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderApplicationService.getOrder(new GetOrderQuery(orderId)));
    }
}

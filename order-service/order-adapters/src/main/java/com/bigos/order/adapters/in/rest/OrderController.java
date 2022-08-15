package com.bigos.order.adapters.in.rest;

import com.bigos.order.domain.ports.dto.order.create.CreateOrderCommand;
import com.bigos.order.domain.ports.dto.order.create.CreateOrderResponse;
import com.bigos.order.domain.ports.dto.order.get.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.get.GetOrderResponse;
import com.bigos.order.domain.ports.in.service.OrderApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderCommand createOrderCommand) {
        return ResponseEntity.ok(orderApplicationService.createOrder(createOrderCommand));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderApplicationService.getOrder(new GetOrderQuery(orderId)));
    }
}

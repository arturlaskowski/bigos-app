package com.bigos.order.adapters.query;


import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.adapters.query.mapper.OrderQueryMapper;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.dto.order.get.GetOrderQuery;
import com.bigos.order.domain.ports.dto.order.get.GetOrderResponse;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class GetOrderQueryHandler {

    private final OrderQueryMapper orderMapper;
    private final OrderRepository orderRepository;

    public GetOrderQueryHandler(OrderQueryMapper orderQueryMapper, OrderRepository orderRepository) {
        this.orderMapper = orderQueryMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public GetOrderResponse getOrderById(GetOrderQuery getOrderQuery) {
        Order order = orderRepository.getOrder(new OrderId(getOrderQuery.orderId()));

        return orderMapper.orderToGetOrderResponse(order);
    }
}

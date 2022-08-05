package com.bigos.order.adapters.out.reposiotry;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.adapters.exception.OrderNotFoundException;
import com.bigos.order.adapters.model.entity.OrderEntity;
import com.bigos.order.adapters.model.mapper.OrderEntityMapper;
import com.bigos.order.domain.model.Order;
import com.bigos.order.domain.ports.out.repository.OrderRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderRepositoryJpa orderRepositoryJpa;
    private final OrderEntityMapper orderEntityMapper;

    public OrderRepositoryImpl(OrderRepositoryJpa orderRepositoryJpa, OrderEntityMapper orderEntityMapper) {
        this.orderRepositoryJpa = orderRepositoryJpa;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public Order save(Order order) {
        return orderEntityMapper.orderEntityToOrder(
                orderRepositoryJpa.save(orderEntityMapper.orderToOrderEntity(order)));
    }

    @Override
    public Order getOrder(OrderId orderId) {
        return orderRepositoryJpa.findById(orderId.id())
                .map(orderEntityMapper::orderEntityToOrder)
                .orElseThrow(() -> new OrderNotFoundException("Could not find order with id: " + orderId.id()));
    }
}

@Repository
interface OrderRepositoryJpa extends CrudRepository<OrderEntity, UUID> {
}

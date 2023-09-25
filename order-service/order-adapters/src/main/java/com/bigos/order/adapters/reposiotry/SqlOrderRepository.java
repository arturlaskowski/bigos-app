package com.bigos.order.adapters.reposiotry;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.order.application.exception.OrderNotFoundException;
import com.bigos.order.domain.core.Order;
import com.bigos.order.domain.port.OrderRepository;
import com.bigos.order.entities.OrderEntity;
import com.bigos.order.entities.OrderEntityCommandMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SqlOrderRepository implements OrderRepository {

    private final OrderRepositoryJpa orderRepositoryJpa;
    private final OrderEntityCommandMapper orderEntityCommandMapper;

    public SqlOrderRepository(OrderRepositoryJpa orderRepositoryJpa, OrderEntityCommandMapper orderEntityCommandMapper) {
        this.orderRepositoryJpa = orderRepositoryJpa;
        this.orderEntityCommandMapper = orderEntityCommandMapper;
    }

    @Override
    public Order save(Order order) {
        return orderEntityCommandMapper.orderEntityToOrder(
                orderRepositoryJpa.save(orderEntityCommandMapper.orderToOrderEntity(order)));
    }

    @Override
    public Order getOrder(OrderId orderId) {
        return orderRepositoryJpa.findById(orderId.id())
                .map(orderEntityCommandMapper::orderEntityToOrder)
                .orElseThrow(() -> new OrderNotFoundException("Could not find order with id: " + orderId.id()));
    }
}

@Repository
interface OrderRepositoryJpa extends CrudRepository<OrderEntity, UUID> {
}

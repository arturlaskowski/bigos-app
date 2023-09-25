package com.bigos.order.adapters.reposiotry;

import com.bigos.order.application.exception.OrderNotFoundException;
import com.bigos.order.application.query.OrderProjection;
import com.bigos.order.application.query.OrderQueryRepository;
import com.bigos.order.entities.OrderEntity;
import com.bigos.order.entities.OrderEntityQueryMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SqlOrderQueryRepository implements OrderQueryRepository {

    private final OrderQueryRepositoryJpa orderQueryRepositoryJpa;
    private final OrderEntityQueryMapper orderEntityQueryMapper;

    public SqlOrderQueryRepository(final OrderQueryRepositoryJpa orderQueryRepositoryJpa, final OrderEntityQueryMapper orderEntityQueryMapper) {
        this.orderQueryRepositoryJpa = orderQueryRepositoryJpa;
        this.orderEntityQueryMapper = orderEntityQueryMapper;
    }

    @Override
    public OrderProjection getOrderProjection(UUID orderId) {
        return orderQueryRepositoryJpa.findById(orderId)
                .map(orderEntityQueryMapper::orderEntityToOrderProjection)
                .orElseThrow(() -> new OrderNotFoundException("Could not find order with id: " + orderId));
    }
}

@Repository
interface OrderQueryRepositoryJpa extends JpaRepository<OrderEntity, UUID> {
}
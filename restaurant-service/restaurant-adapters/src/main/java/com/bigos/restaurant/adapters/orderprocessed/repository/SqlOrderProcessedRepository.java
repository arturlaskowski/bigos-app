package com.bigos.restaurant.adapters.orderprocessed.repository;

import com.bigos.restaurant.domain.orderprocessed.core.OrderProcessed;
import com.bigos.restaurant.domain.orderprocessed.port.OrderProcessedRepository;
import com.bigos.restaurant.entities.orderprocessed.OrderProcessedEntity;
import com.bigos.restaurant.entities.orderprocessed.OrderProcessedEntityMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SqlOrderProcessedRepository implements OrderProcessedRepository {

    private final OrderProcessedRepositoryJpa orderProcessedRepositoryJpa;
    private final OrderProcessedEntityMapper orderProcessedEntityMapper;

    public SqlOrderProcessedRepository(OrderProcessedRepositoryJpa orderProcessedRepositoryJpa, OrderProcessedEntityMapper orderProcessedEntityMapper) {
        this.orderProcessedRepositoryJpa = orderProcessedRepositoryJpa;
        this.orderProcessedEntityMapper = orderProcessedEntityMapper;
    }


    @Override
    public OrderProcessed save(OrderProcessed orderProcessed) {
        return orderProcessedEntityMapper.orderEntityToOrder(
                orderProcessedRepositoryJpa.save(orderProcessedEntityMapper.orderToOrderEntity(orderProcessed)));
    }
}

@Repository
interface OrderProcessedRepositoryJpa extends CrudRepository<OrderProcessedEntity, UUID> {
}

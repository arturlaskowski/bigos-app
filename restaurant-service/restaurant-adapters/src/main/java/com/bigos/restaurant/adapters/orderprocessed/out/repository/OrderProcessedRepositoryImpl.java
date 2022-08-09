package com.bigos.restaurant.adapters.orderprocessed.out.repository;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.restaurant.adapters.orderprocessed.model.entity.OrderProcessedEntity;
import com.bigos.restaurant.adapters.orderprocessed.model.mapper.OrderProcessedEntityMapper;
import com.bigos.restaurant.domain.model.OrderProcessed;
import com.bigos.restaurant.domain.ports.out.repository.OrderProcessedRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderProcessedRepositoryImpl implements OrderProcessedRepository {

    private final OrderProcessedRepositoryJpa orderProcessedRepositoryJpa;
    private final OrderProcessedEntityMapper orderProcessedEntityMapper;

    public OrderProcessedRepositoryImpl(OrderProcessedRepositoryJpa orderProcessedRepositoryJpa, OrderProcessedEntityMapper orderProcessedEntityMapper) {
        this.orderProcessedRepositoryJpa = orderProcessedRepositoryJpa;
        this.orderProcessedEntityMapper = orderProcessedEntityMapper;
    }


    @Override
    public OrderProcessed save(OrderProcessed orderProcessed) {
        return orderProcessedEntityMapper.orderEntityToOrder(
                orderProcessedRepositoryJpa.save(orderProcessedEntityMapper.orderToOrderEntity(orderProcessed)));
    }

    //for tests
    @Override
    public Optional<OrderProcessed> findById(OrderId orderId) {
        return orderProcessedRepositoryJpa.findById(orderId.id()).map(orderProcessedEntityMapper::orderEntityToOrderWithoutItems);

    }
}

@Repository
interface OrderProcessedRepositoryJpa extends CrudRepository<OrderProcessedEntity, UUID> {
}

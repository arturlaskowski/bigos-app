package com.bigos.restaurant.adapters.restaurant.repository;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.application.restaurant.exception.RestaurantNotFoundException;
import com.bigos.restaurant.entities.restaurant.RestaurantEntity;
import com.bigos.restaurant.entities.restaurant.RestaurantEntityMapper;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import com.bigos.restaurant.domain.restaurant.port.RestaurantRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class SqlRestaurantRepository implements RestaurantRepository {

    private final RestaurantRepositoryJpa restaurantRepositoryJpa;
    private final RestaurantEntityMapper restaurantEntityMapper;


    public SqlRestaurantRepository(RestaurantRepositoryJpa restaurantRepositoryJpa, RestaurantEntityMapper restaurantEntityMapper) {
        this.restaurantRepositoryJpa = restaurantRepositoryJpa;
        this.restaurantEntityMapper = restaurantEntityMapper;
    }

    @Override
    public Restaurant getById(RestaurantId restaurantId) {
        return restaurantEntityMapper.restaurantEntityToRestaurant(
                restaurantRepositoryJpa.findById(restaurantId.id())
                        .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not exists: " + restaurantId)));
    }
}

@Repository
interface RestaurantRepositoryJpa extends CrudRepository<RestaurantEntity, UUID> {
}

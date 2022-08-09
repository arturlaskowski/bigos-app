package com.bigos.restaurant.adapters.restaurant.out.repository;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.adapters.restaurant.exception.RestaurantNotFoundException;
import com.bigos.restaurant.adapters.restaurant.model.entity.RestaurantEntity;
import com.bigos.restaurant.adapters.restaurant.model.mapper.RestaurantEntityMapper;
import com.bigos.restaurant.domain.model.Restaurant;
import com.bigos.restaurant.domain.ports.out.repository.RestaurantRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantRepositoryJpa restaurantRepositoryJpa;
    private final RestaurantEntityMapper restaurantEntityMapper;


    public RestaurantRepositoryImpl(RestaurantRepositoryJpa restaurantRepositoryJpa, RestaurantEntityMapper restaurantEntityMapper) {
        this.restaurantRepositoryJpa = restaurantRepositoryJpa;
        this.restaurantEntityMapper = restaurantEntityMapper;
    }

    @Override
    public Restaurant getById(RestaurantId restaurantId) {
        return restaurantEntityMapper.restaurantEntityToRestaurant(
                restaurantRepositoryJpa.findById(restaurantId.id())
                        .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not exists. " + restaurantId)));
    }
}

@Repository
interface RestaurantRepositoryJpa extends CrudRepository<RestaurantEntity, UUID> {
}

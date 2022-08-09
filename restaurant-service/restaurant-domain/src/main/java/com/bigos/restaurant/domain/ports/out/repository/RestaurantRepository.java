package com.bigos.restaurant.domain.ports.out.repository;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.model.Restaurant;

public interface RestaurantRepository {

    Restaurant getById(RestaurantId restaurantId);
}

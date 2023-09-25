package com.bigos.restaurant.domain.restaurant.port;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;

public interface RestaurantRepository {

    Restaurant getById(RestaurantId restaurantId);
}

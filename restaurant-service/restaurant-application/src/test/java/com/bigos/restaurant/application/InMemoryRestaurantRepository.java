package com.bigos.restaurant.application;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import com.bigos.restaurant.domain.restaurant.port.RestaurantRepository;

import java.util.HashMap;
import java.util.Map;

class InMemoryRestaurantRepository implements RestaurantRepository {

    private final Map<RestaurantId, Restaurant> store = new HashMap<>();

    @Override
    public Restaurant getById(RestaurantId restaurantId) {
        return store.get(restaurantId);
    }

    void addRestaurant(Restaurant restaurant) {
        store.put(restaurant.getId(), restaurant);
    }

    void deleteAll() {
        store.clear();
    }
}

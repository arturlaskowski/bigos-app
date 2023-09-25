package com.bigos.restaurant.entities.restaurant;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.restaurant.core.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEntityMapper {

    public Restaurant restaurantEntityToRestaurant(RestaurantEntity restaurantEntity) {
        return new Restaurant(new RestaurantId(restaurantEntity.getId()), restaurantEntity.isAvailable(), restaurantEntity.getName());
    }
}

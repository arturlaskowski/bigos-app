package com.bigos.restaurant.adapters.restaurant.model.mapper;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.adapters.restaurant.model.entity.RestaurantEntity;
import com.bigos.restaurant.domain.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantEntityMapper {

    public Restaurant restaurantEntityToRestaurant(RestaurantEntity restaurantEntity) {
        return new Restaurant(new RestaurantId(restaurantEntity.getId()), restaurantEntity.isAvailable(), restaurantEntity.getName());
    }
}

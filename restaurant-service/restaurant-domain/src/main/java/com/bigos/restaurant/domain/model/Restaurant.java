package com.bigos.restaurant.domain.model;

import com.bigos.common.domain.vo.RestaurantId;
import com.bigos.restaurant.domain.exception.RestaurantDomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Restaurant {

    private RestaurantId restaurantId;

    private boolean available;

    private String name;

    public void validate() {
        if (!available) {
            throw new RestaurantDomainException("Restaurant is unavailable. " + restaurantId);
        }
    }
}

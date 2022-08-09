package com.bigos.restaurant.adapters.restaurant.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(String message) {
        super(message);
    }

}

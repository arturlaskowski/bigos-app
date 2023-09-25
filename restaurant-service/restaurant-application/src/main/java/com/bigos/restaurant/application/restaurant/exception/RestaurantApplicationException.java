package com.bigos.restaurant.application.restaurant.exception;

public class RestaurantApplicationException extends RuntimeException {

    public RestaurantApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

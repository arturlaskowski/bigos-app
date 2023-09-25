package com.bigos.restaurant.application.restaurant;

import com.bigos.restaurant.domain.RestaurantDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainService();
    }
}

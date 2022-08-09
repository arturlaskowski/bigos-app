package com.bigos.restaurant.app;

import com.bigos.restaurant.domain.service.RestaurantDomainService;
import com.bigos.restaurant.domain.service.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}

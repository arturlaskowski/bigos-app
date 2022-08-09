package com.bigos.restaurant.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bigos.restaurant.adapters.orderprocessed.out.repository", "com.bigos.restaurant.adapters.restaurant.out.repository"})
@EntityScan(basePackages = {"com.bigos.restaurant.adapters.orderprocessed.model.entity", "com.bigos.restaurant.adapters.restaurant.model.entity"})
@SpringBootApplication(scanBasePackages = "com.bigos.restaurant")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}

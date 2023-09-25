package com.bigos.restaurant.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bigos.restaurant.adapters.orderprocessed.repository", "com.bigos.restaurant.adapters.restaurant.repository"})
@EntityScan(basePackages = {"com.bigos.restaurant.entities.orderprocessed", "com.bigos.restaurant.entities.restaurant"})
@SpringBootApplication(scanBasePackages = "com.bigos")
public class RestaurantServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}

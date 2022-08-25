package com.bigos.order.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bigos.order.adapters.out.reposiotry", "com.bigos.order.adapters.outbox.repository"})
@EntityScan(basePackages = {"com.bigos.order.adapters.model.entity", "com.bigos.order.adapters.outbox.model.entity"})
@SpringBootApplication(scanBasePackages = "com.bigos")
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

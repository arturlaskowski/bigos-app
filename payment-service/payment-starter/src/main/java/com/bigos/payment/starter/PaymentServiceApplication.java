package com.bigos.payment.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bigos.payment.adapters.payment.repository", "com.bigos.payment.adapters.wallet.repository"})
@EntityScan(basePackages = {"com.bigos.payment.entities"})
@SpringBootApplication(scanBasePackages = "com.bigos")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}

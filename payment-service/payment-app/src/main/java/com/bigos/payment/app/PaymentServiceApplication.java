package com.bigos.payment.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.bigos.payment.adapters.payment.out.repository", "com.bigos.payment.adapters.wallet.out.repository"})
@EntityScan(basePackages = {"com.bigos.payment.adapters.payment.model.entity", "com.bigos.payment.adapters.wallet.model.entity"})
@SpringBootApplication(scanBasePackages = "com.bigos")
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}

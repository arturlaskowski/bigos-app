package com.bigos.payment.adapters.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment-service")
public class PaymentServiceConfigProperties {
    private String orderCreatingEventsTopicName;
    private String paymentStatusEventsTopicName;
}

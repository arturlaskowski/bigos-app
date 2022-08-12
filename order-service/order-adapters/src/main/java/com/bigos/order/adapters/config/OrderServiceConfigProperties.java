package com.bigos.order.adapters.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfigProperties {
    private String orderCreatingEventsTopicName;
    private String paymentStatusEventsTopicName;
    private String orderPaidEventsTopicName;
    private String restaurantApprovalEventsTopicName;
}

package com.bigos.restaurant.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "restaurant-service")
public class RestaurantServiceConfigProperties {
    private String orderPaidEventsTopicName;
    private String restaurantApprovalEventsTopicName;
}

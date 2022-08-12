package com.bigos.infrastructure.kafka.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "kafka-config")
@Configuration
@Data
public class KafkaConfigProperties {

    private String bootstrapServers;
    private Integer numOfPartitions;
    private Integer replicationFactor;
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer {
        private String keySerializerClass;
        private String valueSerializerClass;
        private String compressionType;
        private String acks;
        private Integer batchSize;
        private Integer batchSizeBoostFactor;
        private Integer lingerMs;
        private Integer requestTimeoutMs;
        private Integer retryCount;
    }

    @Data
    public static class Consumer {
        private String keyDeserializer;
        private String valueDeserializer;
        private String autoOffsetReset;
        private String specificAvroReaderKey;
        private String specificAvroReader;
        private Boolean batchListener;
        private Boolean autoStartup;
        private Integer concurrencyLevel;
        private Integer sessionTimeoutMs;
        private Integer heartbeatIntervalMs;
        private Integer maxPollIntervalMs;
        private Long pollTimeoutMs;
        private Integer maxPollRecords;
        private Integer maxPartitionFetchBytesDefault;
        private Integer maxPartitionFetchBytesBoostFactor;
    }
}

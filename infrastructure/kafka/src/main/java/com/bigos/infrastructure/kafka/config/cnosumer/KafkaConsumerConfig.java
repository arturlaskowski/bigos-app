package com.bigos.infrastructure.kafka.config.cnosumer;

import com.bigos.infrastructure.kafka.config.KafkaConfigProperties;
import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDtoJsonDeserializer;
import com.bigos.infrastructure.kafka.config.serialization.TypeDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class KafkaConsumerConfig {

    private final KafkaConfigProperties kafkaConfigProperties;

    public KafkaConsumerConfig(KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfigProperties.getConsumer().getSessionTimeoutMs());
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConfigProperties.getConsumer().getHeartbeatIntervalMs());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConfigProperties.getConsumer().getMaxPollIntervalMs());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                kafkaConfigProperties.getConsumer().getMaxPartitionFetchBytesDefault() *
                        kafkaConfigProperties.getConsumer().getMaxPartitionFetchBytesBoostFactor());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfigProperties.getConsumer().getMaxPollRecords());

        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, MessageKafkaDtoJsonDeserializer.class);
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, TypeDto> kafkaConsumerFactory(MessageKafkaDtoJsonDeserializer messageKafkaDtoJsonDeserializer) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(), messageKafkaDtoJsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TypeDto>> kafkaListenerContainerFactory(@Qualifier("kafkaConsumerFactory") ConsumerFactory<String, TypeDto> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TypeDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.setBatchListener(kafkaConfigProperties.getConsumer().getBatchListener());
        factory.setConcurrency(kafkaConfigProperties.getConsumer().getConcurrencyLevel());
        factory.setAutoStartup(kafkaConfigProperties.getConsumer().getAutoStartup());
        factory.getContainerProperties().setPollTimeout(kafkaConfigProperties.getConsumer().getPollTimeoutMs());
        return factory;
    }
}

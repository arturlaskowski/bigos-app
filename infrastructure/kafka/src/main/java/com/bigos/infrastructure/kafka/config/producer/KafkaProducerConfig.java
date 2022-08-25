package com.bigos.infrastructure.kafka.config.producer;

import com.bigos.infrastructure.kafka.config.KafkaConfigProperties;
import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDto;
import com.bigos.infrastructure.kafka.config.serialization.MessageKafkaDtoJsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaProducerConfig {

    private final KafkaConfigProperties kafkaConfigProperties;

    public KafkaProducerConfig(KafkaConfigProperties kafkaConfigProperties) {
        this.kafkaConfigProperties = kafkaConfigProperties;
    }

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigProperties.getBootstrapServers());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfigProperties.getProducer().getBatchSize() *
                kafkaConfigProperties.getProducer().getBatchSizeBoostFactor());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfigProperties.getProducer().getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConfigProperties.getProducer().getRequestTimeoutMs());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaConfigProperties.getProducer().getRetryCount());

        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, MessageKafkaDtoJsonSerializer.class);
        props.put(ACKS_CONFIG, "all");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        return props;
    }

    @Bean
    public ProducerFactory<String, MessageKafkaDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, MessageKafkaDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

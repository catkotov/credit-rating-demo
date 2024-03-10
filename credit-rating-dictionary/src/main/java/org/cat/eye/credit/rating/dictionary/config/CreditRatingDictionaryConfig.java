package org.cat.eye.credit.rating.dictionary.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.dictionary.IrsRateByCustomerSegmentCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class CreditRatingDictionaryConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String kafkaBootstrapServers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new JsonSerde<IrsRateByCustomerSegmentCode>().getClass());

        return props;
    }

    @Bean
    public ProducerFactory<UUID, IrsRateByCustomerSegmentCode> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<UUID, IrsRateByCustomerSegmentCode> kafkaTemplate() {
        return new KafkaTemplate<UUID, IrsRateByCustomerSegmentCode>(producerFactory());
    }

}

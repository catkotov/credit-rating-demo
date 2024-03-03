package org.cat.eye.credit.rating.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafkaStreams
public class CreditRatingCreationKSConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String kafkaBootstrapServers;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration creditRatingCreationKSConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.UUIDSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>().getClass());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<UUID, CreditProfileCreateRequest> kStream(StreamsBuilder myKStreamBuilder) {
        KStream<UUID, CreditProfileCreateRequest> stream = myKStreamBuilder.stream("credit-rating-request");

        stream.to("app-number-request");

        stream.foreach((key, value) ->
                System.out.println("Принят запрос с ID [" + key + "] от клиента " + value.participant().surname())
        );
        return stream;
    }

}

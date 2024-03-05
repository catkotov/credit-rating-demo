package org.cat.eye.credit.rating.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
import org.cat.eye.credit.rating.model.dictionary.IrsRateByCustomerSegmentCode;
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
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<UUID, CreditProfileCreateRequest> kStream(StreamsBuilder myKStreamBuilder) {
        Consumed<UUID, CreditProfileCreateRequest> consumedOptions = Consumed.with(new WallclockTimestampExtractor());
         return myKStreamBuilder.stream("credit-rating-request", consumedOptions);
    }

    @Bean
    public KStream<UUID, ReserveApplicationNumberResponse> appNumberStream(StreamsBuilder myKStreamBuilder) {
        Consumed<UUID, ReserveApplicationNumberResponse> consumedOptions = Consumed.with(new WallclockTimestampExtractor());
        return myKStreamBuilder.stream("app-number-response", consumedOptions);
    }

    @Bean
    public GlobalKTable<Integer, IrsRateByCustomerSegmentCode> segmentCodeDictionary(StreamsBuilder myKStreamBuilder) {
        return myKStreamBuilder.globalTable(
                "segment-code-table",
                Materialized.
                        <Integer, IrsRateByCustomerSegmentCode, KeyValueStore<Bytes, byte[]>>as("segment-code-table-store")
                        .withKeySerde(Serdes.Integer())
                        .withValueSerde(new JsonSerde<>())
        );
    }

}

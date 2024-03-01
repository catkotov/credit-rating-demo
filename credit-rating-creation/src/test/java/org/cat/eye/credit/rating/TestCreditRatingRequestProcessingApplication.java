package org.cat.eye.credit.rating;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
public class TestCreditRatingRequestProcessingApplication {

    private static final String DOCKER_IMAGE_NAME = "confluentinc/cp-kafka:latest";

    @Bean
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse(DOCKER_IMAGE_NAME));
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration creditRatingCreationKSConfigs(KafkaContainer kafkaContainer) {
        if (!kafkaContainer.isRunning())
            kafkaContainer.start();

        Map<String, Object> props = new HashMap<>();

        CreditRatingRequestProcessingApplicationTest.bootstrap_servers = kafkaContainer.getBootstrapServers();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "dev1");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.UUIDSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(CreditProfileCreateRequest.class).getClass());

        try (AdminClient client = AdminClient.create(props)) {
            client.createTopics(List.of(TopicBuilder.name("credit-rating-request")
                    .partitions(7)
                    .build()));
        } catch (Exception e){
            System.out.println("Не удалось создать топик!!!");
        }

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public NewTopic creditRatingRequest() {
        return TopicBuilder.name("credit-rating-request")
                .partitions(7)
                .build();
    }

}

package org.cat.eye.credit.rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestCreditRatingRequestProcessingApplication {

//    private static final String DOCKER_IMAGE_NAME = "confluentinc/cp-kafka:latest";
    private static final String DOCKER_IMAGE_NAME = "bitnami/kafka:3.4.1";

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse(DOCKER_IMAGE_NAME));
    }

    public static void main(String[] args) {
        SpringApplication.from(CreditRatingRequestProcessingApplication::main).with(TestCreditRatingRequestProcessingApplication.class).run(args);
    }

}

package org.cat.eye.credit.rating;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        classes = {TestCreditRatingRequestProcessingApplication.class}
)
class CreditRatingRequestProcessingApplicationTest {

    static String bootstrap_servers = "kafka:9092";

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> bootstrap_servers);
    }

    @BeforeEach
    void init() {

    }

    @Test
    void contextCreate() {

    }

}
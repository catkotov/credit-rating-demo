package org.cat.eye.credit.rating;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.*;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.application.request.ReserveApplicationNumberRequest;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
import org.cat.eye.credit.rating.model.omni.request.Contact;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.cat.eye.credit.rating.model.omni.request.Participant;
import org.cat.eye.credit.rating.model.omni.response.CreditProfileCreateResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        classes = {CreditRatingRequestProcessingApplicationTestConfiguration.class}
)
//@EmbeddedKafka(
//        topics = {"credit-rating-request", "app-number-request", "app-number-response", "credit-rating-response"},
//        kraft = false)
class CreditRatingRequestProcessingApplicationTest {

    @Autowired
    private StreamsBuilderFactoryBean defaultKafkaStreamsBuilder;

    private TopologyTestDriver testDriver;
    private TestInputTopic<UUID, CreditProfileCreateRequest> inputTopic;
    private TestOutputTopic<UUID, ReserveApplicationNumberRequest> appNumberOutputTopic;
    private TestInputTopic<UUID, ReserveApplicationNumberResponse> appNumberInputTopic;
    private TestOutputTopic<UUID, CreditProfileCreateResponse> outputTopic;

    @BeforeEach
    void init() {
        Properties props = new Properties();
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.UUIDSerde.class.getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<CreditProfileCreateRequest>().getClass().getName());
        testDriver = new TopologyTestDriver(defaultKafkaStreamsBuilder.getTopology(), props);
        inputTopic = testDriver.createInputTopic("credit-rating-request", new UUIDSerializer(), new JsonSerde<>());
        appNumberOutputTopic = testDriver.createOutputTopic("app-number-request", new UUIDDeserializer(), new JsonSerde<>());
        appNumberInputTopic = testDriver.createInputTopic("app-number-response", new UUIDSerializer(), new JsonSerde<>());
        outputTopic = testDriver.createOutputTopic("credit-rating-response", new UUIDDeserializer(), new JsonSerde<>());
    }

    @AfterEach
    void shutDown() {
        testDriver.close();
    }

    @Test
    void contextCreate() throws ExecutionException, InterruptedException {

        Contact contact = new Contact("full", "+7 777 777 77 77", null);
        Participant participant = new Participant("Петров", "Петр", "Петрович", contact);
        CreditProfileCreateRequest request = new CreditProfileCreateRequest(UUID.randomUUID().toString(), participant);

        inputTopic.pipeInput(UUID.fromString(request.appSequence()), request);

        KeyValue<UUID, ReserveApplicationNumberRequest> appNumberRequest = appNumberOutputTopic.readKeyValue();
        System.out.println("Принят запрос на выделения номера заявления от " + appNumberRequest.key);
        ReserveApplicationNumberResponse appNumberResponse = new ReserveApplicationNumberResponse(1234567890L);
        appNumberInputTopic.pipeInput(appNumberRequest.key, appNumberResponse);

        KeyValue<UUID, CreditProfileCreateResponse> response = outputTopic.readKeyValue();
        System.out.println("Создан кредитный профиль: appNumber = [" + response.value.data().appNumber() + "]");

    }

}
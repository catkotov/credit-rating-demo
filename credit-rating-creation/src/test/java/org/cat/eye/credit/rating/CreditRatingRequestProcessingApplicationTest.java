package org.cat.eye.credit.rating;

import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.apache.kafka.streams.*;
import org.cat.eye.credit.rating.model.JsonSerde;
import org.cat.eye.credit.rating.model.application.request.ReserveApplicationNumberRequest;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
import org.cat.eye.credit.rating.model.dictionary.IrsRateByCustomerSegmentCode;
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
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {"spring.main.allow-bean-definition-overriding=true"},
        classes = {CreditRatingRequestProcessingApplicationTestConfiguration.class}
)
@EmbeddedKafka(
        topics = {"credit-rating-request", "app-number-request", "app-number-response", "credit-rating-response"},
        kraft = false
)
class CreditRatingRequestProcessingApplicationTest {

    @Autowired
    private StreamsBuilderFactoryBean defaultKafkaStreamsBuilder;

    private TopologyTestDriver testDriver;
    private TestInputTopic<UUID, CreditProfileCreateRequest> inputTopic;
    private TestOutputTopic<UUID, ReserveApplicationNumberRequest> appNumberOutputTopic;
    private TestInputTopic<UUID, ReserveApplicationNumberResponse> appNumberInputTopic;
    private TestOutputTopic<UUID, CreditProfileCreateResponse> outputTopic;
    private TestInputTopic<Integer, IrsRateByCustomerSegmentCode> segmentCodeTopic;

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
        segmentCodeTopic = testDriver.createInputTopic("segment-code-table", new IntegerSerializer(), new JsonSerde<>());

        IrsRateByCustomerSegmentCode segmentCode_1 = new IrsRateByCustomerSegmentCode(2, UUID.randomUUID(), 22, 2.22, LocalDate.now().minusDays(1), LocalDate.now().plusDays(22));
        IrsRateByCustomerSegmentCode segmentCode_2 = new IrsRateByCustomerSegmentCode(1, UUID.randomUUID(), 11, 1.11, LocalDate.now().minusDays(1), LocalDate.now().plusDays(11));

        segmentCodeTopic.pipeInput(segmentCode_1.customerSegmentCode(), segmentCode_1);
        segmentCodeTopic.pipeInput(segmentCode_2.customerSegmentCode(), segmentCode_2);
    }

    @AfterEach
    void shutDown() {
        testDriver.close();
    }

    @Test
    void contextCreate() throws InterruptedException {

        Contact contact = new Contact("full", "+7 777 777 77 77", null);
        Participant participant = new Participant("Петров", "Петр", "Петрович", contact);
        CreditProfileCreateRequest request = new CreditProfileCreateRequest(UUID.randomUUID().toString(), participant);

        inputTopic.pipeInput(UUID.fromString(request.appSequence()), request);

        KeyValue<UUID, ReserveApplicationNumberRequest> appNumberRequest = appNumberOutputTopic.readKeyValue();
        assertNotNull(appNumberRequest.value);
        System.out.println("Принят запрос на выделения номера заявления от " + appNumberRequest.key);
        ReserveApplicationNumberResponse appNumberResponse = new ReserveApplicationNumberResponse(1234567890L);
        appNumberInputTopic.pipeInput(appNumberRequest.key, appNumberResponse);

        KeyValue<UUID, CreditProfileCreateResponse> response = outputTopic.readKeyValue();
        System.out.println("Создан кредитный профиль: appNumber = [" + response.value.data().appNumber() + "]");
        System.out.println("Rate: [" + response.value.data().rate() + "]");


        Contact contact_2 = new Contact("full", "+7 999 999 99 99", null);
        Participant participant_2 = new Participant("Сидоров", "Сидор", "Сидорович", contact_2);
        CreditProfileCreateRequest request_2 = new CreditProfileCreateRequest(UUID.randomUUID().toString(), participant_2);

        inputTopic.pipeInput(UUID.fromString(request_2.appSequence()), request_2);

        appNumberRequest = appNumberOutputTopic.readKeyValue();
        assertNotNull(appNumberRequest.value);
        System.out.println("Принят запрос на выделения номера заявления от " + appNumberRequest.key);

        Thread.sleep(Duration.ofSeconds(10));

        appNumberResponse = new ReserveApplicationNumberResponse(9876543210L);
        appNumberInputTopic.pipeInput(appNumberRequest.key, appNumberResponse);

        response = outputTopic.readKeyValue();
        System.out.println("Создан кредитный профиль: appNumber = [" + response.value.data().appNumber() + "]");
        System.out.println("Rate: [" + response.value.data().rate() + "]");

    }

}
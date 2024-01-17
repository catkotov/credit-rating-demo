package org.cat.eye.credit.rating.model;

import org.cat.eye.credit.rating.model.omni.request.Contact;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.cat.eye.credit.rating.model.omni.request.Participant;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerdeTest {

    @Test
    void deserialize() {
    }

    @Test
    void serialize() {

        Contact contact = new Contact("full", "+7 777 777 77 77", null);
        Participant participant = new Participant("Петров", "Петр", "Петрович", contact);
        CreditProfileCreateRequest request = new CreditProfileCreateRequest(UUID.randomUUID().toString(), participant);

        try (JsonSerde<CreditProfileCreateRequest> serde = new JsonSerde<>()) {
            byte[] bytes = serde.serialize("", request);
            CreditProfileCreateRequest result = serde.deserialize("", bytes);
            assertNotNull(result);
            assertEquals(request, result);
        }
    }

}
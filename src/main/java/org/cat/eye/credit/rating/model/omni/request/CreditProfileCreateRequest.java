package org.cat.eye.credit.rating.model.omni.request;

public record CreditProfileCreateRequest(
        String appSequence,
        Participant participant
) {
}

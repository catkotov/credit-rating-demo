package org.cat.eye.credit.rating.model.omni.request;

import org.cat.eye.credit.rating.model.JSONSerdeCompatible;

public record CreditProfileCreateRequest(
        String appSequence,
        Participant participant
) implements JSONSerdeCompatible {
}

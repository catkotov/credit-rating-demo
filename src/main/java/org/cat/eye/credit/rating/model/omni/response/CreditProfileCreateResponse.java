package org.cat.eye.credit.rating.model.omni.response;

public record CreditProfileCreateResponse(
        Status status,
        long actualTimestamp,
        CreditProfileData data
) {
}

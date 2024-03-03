package org.cat.eye.credit.rating.model.omni.response;

public record CreditProfileData(
        Long appNumber,
        String rawId,
        State state
) {
}

package org.cat.eye.credit.rating.model.omni.response;

public record CreditProfileData(
        Long appNumber,
        Integer segmentCodeId,
        Double rate,
        String rawId,
        State state
) {
}

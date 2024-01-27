package org.cat.eye.credit.rating.model.omni.response;

import org.cat.eye.credit.rating.model.JSONSerdeCompatible;

public record CreditProfileCreateResponse(
        Status status,
        long actualTimestamp,
        CreditProfileData data
) implements JSONSerdeCompatible {
}

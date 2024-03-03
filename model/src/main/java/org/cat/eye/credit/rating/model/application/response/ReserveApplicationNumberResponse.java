package org.cat.eye.credit.rating.model.application.response;

import org.cat.eye.credit.rating.model.JSONSerdeCompatible;

public record ReserveApplicationNumberResponse(
        Long reservedAppNumber
) implements JSONSerdeCompatible {
}

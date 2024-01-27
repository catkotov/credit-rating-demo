package org.cat.eye.credit.rating.model.omni.response;

import java.util.List;

public record State(
        String code,
        String name,
        Boolean isFinal,
        List<PaymentParameter> parameters
) {
}

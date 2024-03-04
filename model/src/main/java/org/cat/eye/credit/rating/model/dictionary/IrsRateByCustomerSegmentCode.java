package org.cat.eye.credit.rating.model.dictionary;

import org.cat.eye.credit.rating.model.JSONSerdeCompatible;

import java.time.LocalDate;
import java.util.UUID;

public record IrsRateByCustomerSegmentCode(
        Integer customerSegmentCode,
        UUID id,
        Integer serviceCode,
        Double rate,
        LocalDate startDate,
        LocalDate endDate
) implements JSONSerdeCompatible {
}

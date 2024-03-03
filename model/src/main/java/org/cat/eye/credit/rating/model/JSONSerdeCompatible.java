package org.cat.eye.credit.rating.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cat.eye.credit.rating.model.application.request.ReserveApplicationNumberRequest;
import org.cat.eye.credit.rating.model.application.response.ReserveApplicationNumberResponse;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;
import org.cat.eye.credit.rating.model.omni.response.CreditProfileCreateResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_t")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditProfileCreateRequest.class, name = "rcpc"),
        @JsonSubTypes.Type(value = ReserveApplicationNumberRequest.class, name = "ran"),
        @JsonSubTypes.Type(value = ReserveApplicationNumberResponse.class, name = "anr"),
        @JsonSubTypes.Type(value = CreditProfileCreateResponse.class, name = "cpcr")
})
public interface JSONSerdeCompatible {
}

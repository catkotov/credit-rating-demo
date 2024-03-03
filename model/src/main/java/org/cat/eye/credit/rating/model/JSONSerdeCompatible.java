package org.cat.eye.credit.rating.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cat.eye.credit.rating.model.application.request.ReserveApplicationNumberRequest;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_t")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditProfileCreateRequest.class, name = "cpcr"),
        @JsonSubTypes.Type(value = ReserveApplicationNumberRequest.class, name = "ranr")
})
public interface JSONSerdeCompatible {
}

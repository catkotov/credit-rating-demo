package org.cat.eye.credit.rating.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cat.eye.credit.rating.model.omni.request.CreditProfileCreateRequest;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_t")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditProfileCreateRequest.class, name = "cpcr")
})
public interface JSONSerdeCompatible {
}

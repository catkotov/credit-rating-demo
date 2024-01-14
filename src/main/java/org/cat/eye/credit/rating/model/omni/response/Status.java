package org.cat.eye.credit.rating.model.omni.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    SUCCESS("success"),
    ERROR("error");

    private final String status;
}

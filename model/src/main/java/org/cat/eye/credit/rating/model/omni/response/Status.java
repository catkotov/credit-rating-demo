package org.cat.eye.credit.rating.model.omni.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    SUCCESS("success"),
    ERROR("error");

    private final String status;

}

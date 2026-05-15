package com.focela.platform.infra.enums.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API exception data process status
 */
@AllArgsConstructor
@Getter
public enum ApiErrorLogProcessStatusEnum {

    INIT(0, "Not processed"),
    DONE(1, "Processed"),
    IGNORE(2, "Ignored");

    /**
     * Status
     */
    private final Integer status;
    /**
     * Resource type name
     */
    private final String name;

}

package com.focela.platform.module.infra.enums.job;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Job log status enum
 */
@Getter
@AllArgsConstructor
public enum JobLogStatusEnum {

    RUNNING(0), // Running
    SUCCESS(1), // Succeeded
    FAILURE(2); // Failed

    /**
     * Status
     */
    private final Integer status;

}

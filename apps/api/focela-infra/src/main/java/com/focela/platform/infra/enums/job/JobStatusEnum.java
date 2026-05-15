package com.focela.platform.infra.enums.job;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quartz.impl.jdbcjobstore.Constants;

import java.util.Collections;
import java.util.Set;

/**
 * Job status enum
 */
@Getter
@AllArgsConstructor
public enum JobStatusEnum {

    /**
     * Initializing
     */
    INIT(0, Collections.emptySet()),
    /**
     * Enabled
     */
    NORMAL(1, Sets.newHashSet(Constants.STATE_WAITING, Constants.STATE_ACQUIRED, Constants.STATE_BLOCKED)),
    /**
     * Paused
     */
    STOP(2, Sets.newHashSet(Constants.STATE_PAUSED, Constants.STATE_PAUSED_BLOCKED));

    /**
     * Status
     */
    private final Integer status;
    /**
     * Corresponding Quartz trigger status set
     */
    private final Set<String> quartzStates;

}

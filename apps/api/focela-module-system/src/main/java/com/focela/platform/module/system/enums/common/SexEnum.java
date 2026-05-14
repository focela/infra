package com.focela.platform.module.system.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Gender enum values
 */
@Getter
@AllArgsConstructor
public enum SexEnum {

    /** Male */
    MALE(1),
    /** Female */
    FEMALE(2),
    /* Unknown */
    UNKNOWN(0);

    /**
     * Gender
     */
    private final Integer sex;

}

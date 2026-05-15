package com.focela.platform.module.system.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Notice type
 */
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {

    NOTICE(1),
    ANNOUNCEMENT(2);

    /**
     * Type
     */
    private final Integer type;

}

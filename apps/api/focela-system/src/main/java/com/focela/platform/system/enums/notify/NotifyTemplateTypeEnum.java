package com.focela.platform.system.enums.notify;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Notify template type enum
 */
@Getter
@AllArgsConstructor
public enum NotifyTemplateTypeEnum {

    /**
     * System message
     */
    SYSTEM_MESSAGE(2),
    /**
     * Notification message
     */
    NOTIFICATION_MESSAGE(1);

    private final Integer type;

}

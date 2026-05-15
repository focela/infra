package com.focela.platform.module.system.api.notify.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Notification message sent to an Admin or Member user
 */
@Data
public class NotifySendSingleToUserRpcRequest {

    /**
     * User ID
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;

    /**
     * Notification template ID
     */
    @NotEmpty(message = "notify message template ID must not be blank")
    private String templateCode;

    /**
     * Notification template parameters
     */
    private Map<String, Object> templateParams;
}

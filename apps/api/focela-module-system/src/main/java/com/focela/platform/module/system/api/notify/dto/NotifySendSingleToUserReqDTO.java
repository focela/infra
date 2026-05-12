package com.focela.platform.module.system.api.notify.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * 站内信发送给 Admin 或者 Member 用户
 */
@Data
public class NotifySendSingleToUserReqDTO {

    /**
     * 用户编号
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;

    /**
     * 站内信模板编号
     */
    @NotEmpty(message = "notify message template ID must not be blank")
    private String templateCode;

    /**
     * 站内信模板参数
     */
    private Map<String, Object> templateParams;
}

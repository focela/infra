package com.focela.platform.system.api.sms.dto.send;

import com.focela.platform.common.validation.Mobile;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * SMS sent to an Admin or Member user
 */
@Data
public class SmsSendSingleToUserRpcRequest {

    /**
     * User ID
     */
    private Long userId;
    /**
     * Mobile number
     */
    @Mobile
    private String mobile;
    /**
     * SMS template ID
     */
    @NotEmpty(message = "SMS template ID must not be blank")
    private String templateCode;
    /**
     * SMS template parameters
     */
    private Map<String, Object> templateParams;

}

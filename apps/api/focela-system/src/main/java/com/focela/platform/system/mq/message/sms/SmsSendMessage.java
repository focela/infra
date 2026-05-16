package com.focela.platform.system.mq.message.sms;

import com.focela.platform.common.core.KeyValue;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * SMS send message
 */
@Data
public class SmsSendMessage {

    /**
     * SMS log ID
     */
    @NotNull(message = "SMS log ID must not be blank")
    private Long logId;
    /**
     * Mobile number
     */
    @NotNull(message = "mobile number must not be blank")
    private String mobile;
    /**
     * SMS channel ID
     */
    @NotNull(message = "SMS channel ID must not be blank")
    private Long channelId;
    /**
     * SMS API template ID
     */
    @NotNull(message = "SMS API template ID must not be blank")
    private String apiTemplateId;
    /**
     * SMS template parameters
     */
    private List<KeyValue<String, Object>> templateParams;

}

package com.focela.platform.system.mq.message.sms;

import com.focela.platform.common.core.KeyValue;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 短信发送消息
 */
@Data
public class SmsSendMessage {

    /**
     * 短信日志编号
     */
    @NotNull(message = "SMS log ID must not be blank")
    private Long logId;
    /**
     * 手机号
     */
    @NotNull(message = "mobile number must not be blank")
    private String mobile;
    /**
     * 短信渠道编号
     */
    @NotNull(message = "SMS channel ID must not be blank")
    private Long channelId;
    /**
     * 短信 API 的模板编号
     */
    @NotNull(message = "SMS API template ID must not be blank")
    private String apiTemplateId;
    /**
     * 短信模板参数
     */
    private List<KeyValue<String, Object>> templateParams;

}

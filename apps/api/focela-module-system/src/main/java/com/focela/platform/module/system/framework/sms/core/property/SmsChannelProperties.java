package com.focela.platform.module.system.framework.sms.core.property;

import com.focela.platform.module.system.framework.sms.core.enums.SmsChannelEnum;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 短信渠道配置类
 *
 * @since 2021/1/25 17:01
 */
@Data
@Validated
public class SmsChannelProperties {

    /**
     * 渠道编号
     */
    @NotNull(message = "SMS channel ID must not be blank")
    private Long id;
    /**
     * 短信签名
     */
    @NotEmpty(message = "SMS signature must not be blank")
    private String signature;
    /**
     * 渠道编码
     *
     * 枚举 {@link SmsChannelEnum}
     */
    @NotEmpty(message = "channel code must not be blank")
    private String code;
    /**
     * 短信 API 的账号
     */
    @NotEmpty(message = "SMS API account must not be blank")
    private String apiKey;
    /**
     * 短信 API 的密钥
     */
    @NotEmpty(message = "SMS API secret must not be blank")
    private String apiSecret;
    /**
     * 短信发送回调 URL
     */
    private String callbackUrl;

}

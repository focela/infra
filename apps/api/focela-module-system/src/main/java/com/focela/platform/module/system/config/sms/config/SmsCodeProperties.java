package com.focela.platform.module.system.config.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@ConfigurationProperties(prefix = "focela.sms-code")
@Validated
@Data
public class SmsCodeProperties {

    /**
     * 过期时间
     */
    @NotNull(message = "expires at must not be blank")
    private Duration expireTimes;
    /**
     * 短信发送频率
     */
    @NotNull(message = "SMS send 频率must not be blank")
    private Duration sendFrequency;
    /**
     * 每日发送最大数量
     */
    @NotNull(message = "each day send most 大count must not be blank")
    private Integer sendMaximumQuantityPerDay;
    /**
     * 验证码最小值
     */
    @NotNull(message = "CAPTCHA min value must not be blank")
    private Integer beginCode;
    /**
     * 验证码最大值
     */
    @NotNull(message = "CAPTCHA max value must not be blank")
    private Integer endCode;

}

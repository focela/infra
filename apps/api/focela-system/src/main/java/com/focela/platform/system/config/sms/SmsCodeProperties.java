package com.focela.platform.system.config.sms;

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
     * Expiration time
     */
    @NotNull(message = "expires at must not be blank")
    private Duration expireTimes;
    /**
     * SMS send frequency
     */
    @NotNull(message = "SMS send frequency must not be blank")
    private Duration sendFrequency;
    /**
     * Maximum number of sends per day
     */
    @NotNull(message = "maximum sends per day must not be blank")
    private Integer sendMaximumQuantityPerDay;
    /**
     * Verification code minimum value
     */
    @NotNull(message = "CAPTCHA min value must not be blank")
    private Integer beginCode;
    /**
     * Verification code maximum value
     */
    @NotNull(message = "CAPTCHA max value must not be blank")
    private Integer endCode;

}

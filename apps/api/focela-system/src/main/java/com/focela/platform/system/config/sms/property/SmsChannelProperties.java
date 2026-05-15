package com.focela.platform.system.config.sms.property;

import com.focela.platform.system.config.sms.enums.SmsChannelEnum;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * SMS channel configuration class
 *
 * @since 2021/1/25 17:01
 */
@Data
@Validated
public class SmsChannelProperties {

    /**
     * Channel ID
     */
    @NotNull(message = "SMS channel ID must not be blank")
    private Long id;
    /**
     * SMS signature
     */
    @NotEmpty(message = "SMS signature must not be blank")
    private String signature;
    /**
     * Channel code
     *
     * Enum {@link SmsChannelEnum}
     */
    @NotEmpty(message = "channel code must not be blank")
    private String code;
    /**
     * SMS API account
     */
    @NotEmpty(message = "SMS API account must not be blank")
    private String apiKey;
    /**
     * SMS API secret
     */
    @NotEmpty(message = "SMS API secret must not be blank")
    private String apiSecret;
    /**
     * SMS send callback URL
     */
    private String callbackUrl;

}

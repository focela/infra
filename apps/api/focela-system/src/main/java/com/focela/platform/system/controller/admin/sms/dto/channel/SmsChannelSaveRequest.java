package com.focela.platform.system.controller.admin.sms.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Admin - SMS channel create /update Request VO")
@Data
public class SmsChannelSaveRequest {

    @Schema(description = "ID", example = "1024")
    private Long id;

    @Schema(description = "SMS signature", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotNull(message = "SMS signature must not be blank")
    private String signature;

    @Schema(description = "Channel code, see SmsChannelEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN")
    @NotNull(message = "channel code must not be blank")
    private String code;

    @Schema(description = "Enable status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "enable status must not be blank")
    private Integer status;

    @Schema(description = "Remarks", example = "tasty!")
    private String remark;

    @Schema(description = "SMS API account", requiredMode = Schema.RequiredMode.REQUIRED, example = "focela")
    @NotNull(message = "SMS API account must not be blank")
    private String apiKey;

    @Schema(description = "SMS API secret", example = "yuanma")
    private String apiSecret;

    @Schema(description = "SMS send callback URL", example = "https://www.example.com")
    @URL(message = "callback URL format is invalid")
    private String callbackUrl;

}

package com.focela.platform.module.system.controller.admin.sms.dto.channel;

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
    @NotNull(message = "短信签名不能为空")
    private String signature;

    @Schema(description = "Channel code, see SmsChannelEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN")
    @NotNull(message = "渠道编码不能为空")
    private String code;

    @Schema(description = "Enable status", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "启用状态不能为空")
    private Integer status;

    @Schema(description = "Remarks", example = "tasty!")
    private String remark;

    @Schema(description = "SMS API account", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao")
    @NotNull(message = "短信 API 的账号不能为空")
    private String apiKey;

    @Schema(description = "SMS API secret", example = "yuanma")
    private String apiSecret;

    @Schema(description = "SMS send callback URL", example = "https://www.example.com")
    @URL(message = "回调 URL 格式不正确")
    private String callbackUrl;

}

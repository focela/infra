package com.focela.platform.module.system.controller.admin.sms.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "Admin - SMS channel Response VO")
@Data
public class SmsChannelResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "SMS signature", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    @NotNull(message = "短信签名不能为空")
    private String signature;

    @Schema(description = "Channel code, see SmsChannelEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN")
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

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

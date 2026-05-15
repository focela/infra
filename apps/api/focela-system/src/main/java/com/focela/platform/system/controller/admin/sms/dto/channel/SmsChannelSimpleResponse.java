package com.focela.platform.system.controller.admin.sms.dto.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - SMS channel simplified Response VO")
@Data
public class SmsChannelSimpleResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "SMS signature", requiredMode = Schema.RequiredMode.REQUIRED, example = "Acme")
    private String signature;

    @Schema(description = "Channel code, see SmsChannelEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "YUN_PIAN")
    private String code;

}

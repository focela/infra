package com.focela.platform.module.system.controller.admin.mail.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - email account simplified Response VO")
@Data
public class MailAccountSimpleResponse {

    @Schema(description = "Email ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "Email", requiredMode = Schema.RequiredMode.REQUIRED, example = "768541388@qq.com")
    private String mail;

}

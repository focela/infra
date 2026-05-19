package com.focela.platform.system.controller.admin.mail.response.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - email template simplified Response")
@Data
public class MailTemplateSimpleResponse {

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "template name", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String name;

}

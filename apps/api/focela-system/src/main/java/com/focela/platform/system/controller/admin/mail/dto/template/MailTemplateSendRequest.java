package com.focela.platform.system.controller.admin.mail.dto.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Schema(description = "Admin - email send Req VO")
@Data
public class MailTemplateSendRequest {

    @Schema(description = "receive email", requiredMode = Schema.RequiredMode.REQUIRED, example = "[user1@example.com, user2@example.com]")
    @NotEmpty(message = "receive email must not be blank")
    private List<String> toMails;

    @Schema(description = "CC", requiredMode = Schema.RequiredMode.REQUIRED, example = "[user3@example.com, user4@example.com]")
    private List<String> ccMails;

    @Schema(description = "BCC", requiredMode = Schema.RequiredMode.REQUIRED, example = "[user5@example.com, user6@example.com]")
    private List<String> bccMails;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    @NotNull(message = "template code must not be blank")
    private String templateCode;

    @Schema(description = "Template params")
    private Map<String, Object> templateParams;

}

package com.focela.platform.system.controller.admin.mail.response.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "Admin - email log Response")
@Data
public class MailLogResponse {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31020")
    private Long id;

    @Schema(description = "User ID", example = "30883")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", example = "2")
    private Byte userType;

    @Schema(description = "receive email address", requiredMode = Schema.RequiredMode.REQUIRED, example = "user1@example.com, user2@example.com")
    private List<String> toMails;

    @Schema(description = "CC address", requiredMode = Schema.RequiredMode.REQUIRED, example = "user3@example.com, user4@example.com")
    private List<String> ccMails;

    @Schema(description = "BCC address", requiredMode = Schema.RequiredMode.REQUIRED, example = "user5@example.com, user6@example.com")
    private List<String> bccMails;

    @Schema(description = "Email account ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18107")
    private Long accountId;

    @Schema(description = "send email address", requiredMode = Schema.RequiredMode.REQUIRED, example = "85757@qq.com")
    private String fromMail;

    @Schema(description = "Template ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5678")
    private Long templateId;

    @Schema(description = "Template code", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_01")
    private String templateCode;

    @Schema(description = "Sender name", example = "John Doe")
    private String templateNickname;

    @Schema(description = "Email subject", requiredMode = Schema.RequiredMode.REQUIRED, example = "test title")
    private String templateTitle;

    @Schema(description = "Email content", requiredMode = Schema.RequiredMode.REQUIRED, example = "test content")
    private String templateContent;

    @Schema(description = "Email params", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> templateParams;

    @Schema(description = "Send status, see MailSendStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Byte sendStatus;

    @Schema(description = "Sent time")
    private LocalDateTime sendTime;

    @Schema(description = "send return message ID", example = "28568")
    private String sendMessageId;

    @Schema(description = "send exception")
    private String sendException;

    @Schema(description = "Created time", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}

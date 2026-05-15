package com.focela.platform.module.system.controller.admin.mail.dto.log;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - email log page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailLogPageRequest extends PageParam {

    @Schema(description = "User ID", example = "30883")
    private Long userId;

    @Schema(description = "User type, see UserTypeEnum", example = "2")
    private Integer userType;

    @Schema(description = "receive email address, fuzzy match", example = "76854@qq.com")
    private String toMail;

    @Schema(description = "Email account ID", example = "18107")
    private Long accountId;

    @Schema(description = "Template ID", example = "5678")
    private Long templateId;

    @Schema(description = "Send status, see MailSendStatusEnum", example = "1")
    private Integer sendStatus;

    @Schema(description = "Sent time")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] sendTime;

}

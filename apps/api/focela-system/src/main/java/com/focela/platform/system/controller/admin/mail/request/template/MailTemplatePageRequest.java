package com.focela.platform.system.controller.admin.mail.request.template;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - email template page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MailTemplatePageRequest extends PageParam {

    @Schema(description = "Status, see CommonStatusEnum", example = "1")
    private Integer status;

    @Schema(description = "code, fuzzy match", example = "code_1024")
    private String code;

    @Schema(description = "name, fuzzy match", example = "Bob")
    private String name;

    @Schema(description = "account ID", example = "2048")
    private Long accountId;

    @Schema(description = "Created time")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

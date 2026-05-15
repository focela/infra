package com.focela.platform.system.controller.admin.sms.dto.template;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - SMS template page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsTemplatePageRequest extends PageParam {

    @Schema(description = "SMS signature", example = "1")
    private Integer type;

    @Schema(description = "enable status", example = "1")
    private Integer status;

    @Schema(description = "template code, fuzzy match", example = "test_01")
    private String code;

    @Schema(description = "template content, fuzzy match", example = "hello, {name}. you tall too {like}!")
    private String content;

    @Schema(description = "SMS API template ID, fuzzy match", example = "4383920")
    private String apiTemplateId;

    @Schema(description = "SMS channel ID", example = "10")
    private Long channelId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Created time")
    private LocalDateTime[] createTime;

}

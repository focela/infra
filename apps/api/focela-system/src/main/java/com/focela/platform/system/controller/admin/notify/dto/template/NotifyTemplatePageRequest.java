package com.focela.platform.system.controller.admin.notify.dto.template;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - notify template page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotifyTemplatePageRequest extends PageParam {

    @Schema(description = "Template code", example = "test_01")
    private String code;

    @Schema(description = "Template name", example = "I am name")
    private String name;

    @Schema(description = "Status, see CommonStatusEnum", example = "1")
    private Integer status;

    @Schema(description = "Created time")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

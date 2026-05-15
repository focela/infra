package com.focela.platform.system.controller.admin.notify.dto.message;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - Notify message page request")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotifyMessagePageRequest extends PageParam {

    @Schema(description = "User ID", example = "25025")
    private Long userId;

    @Schema(description = "User type", example = "1")
    private Integer userType;

    @Schema(description = "Template code", example = "test_01")
    private String templateCode;

    @Schema(description = "Template type", example = "2")
    private Integer templateType;

    @Schema(description = "Created time")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

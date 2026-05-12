package com.focela.platform.module.system.controller.admin.permission.dto.role;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - role page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageRequest extends PageParam {

    @Schema(description = "role name, fuzzy match", example = "Acme")
    private String name;

    @Schema(description = "role code, fuzzy match", example = "yudao")
    private String code;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

    @Schema(description = "Created time", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

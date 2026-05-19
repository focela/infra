package com.focela.platform.system.controller.admin.user.request;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - user page Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPageRequest extends PageParam {

    @Schema(description = "user account, fuzzy match", example = "focela")
    private String username;

    @Schema(description = "mobile number, fuzzy match", example = "focela")
    private String mobile;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

    @Schema(description = "Created time", example = "[2022-07-01 00:00:00, 2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "department ID, and filter child department", example = "1024")
    private Long deptId;

    @Schema(description = "Role ID", example = "1024")
    private Long roleId;

}

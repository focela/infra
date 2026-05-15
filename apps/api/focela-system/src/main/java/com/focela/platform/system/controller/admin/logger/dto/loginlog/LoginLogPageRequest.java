package com.focela.platform.system.controller.admin.logger.dto.loginlog;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - login log page list Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogPageRequest extends PageParam {

    @Schema(description = "user IP, fuzzy match", example = "127.0.0.1")
    private String userIp;

    @Schema(description = "user account, fuzzy match", example = "Acme")
    private String username;

    @Schema(description = "operation status", example = "true")
    private Boolean status;

    @Schema(description = "Login time", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

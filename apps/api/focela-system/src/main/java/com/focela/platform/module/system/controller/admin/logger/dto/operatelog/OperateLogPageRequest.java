package com.focela.platform.module.system.controller.admin.logger.dto.operatelog;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - operation log page list Request VO")
@Data
public class OperateLogPageRequest extends PageParam {

    @Schema(description = "User ID", example = "Acme")
    private Long userId;

    @Schema(description = "Business ID", example = "1")
    private Long bizId;

    @Schema(description = "operation module, fuzzy match", example = "order")
    private String type;

    @Schema(description = "operation name, fuzzy match", example = "create order")
    private String subType;

    @Schema(description = "operation detail, fuzzy match", example = "update ID as 1 user info")
    private String action;

    @Schema(description = "Start time", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}

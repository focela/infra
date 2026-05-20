package com.focela.platform.system.controller.admin.tenant.request.tenantpackage;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - tenant package page Request")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPackagePageRequest extends PageParam {

    @Schema(description = "Package name", example = "VIP")
    private String name;

    @Schema(description = "Status", example = "1")
    private Integer status;

    @Schema(description = "Remarks", example = "good")
    private String remark;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Created time")
    private LocalDateTime[] createTime;
}

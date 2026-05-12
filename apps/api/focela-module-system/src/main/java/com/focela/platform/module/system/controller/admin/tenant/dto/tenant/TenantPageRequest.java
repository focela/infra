package com.focela.platform.module.system.controller.admin.tenant.dto.tenant;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.focela.platform.framework.common.utils.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "Admin - tenant page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TenantPageRequest extends PageParam {

    @Schema(description = "Tenant name", example = "Acme")
    private String name;

    @Schema(description = "Contact name", example = "Alice")
    private String contactName;

    @Schema(description = "Contact phone", example = "15601691300")
    private String contactMobile;

    @Schema(description = "tenant status (0active 1disabled)", example = "1")
    private Integer status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "Created time")
    private LocalDateTime[] createTime;

}

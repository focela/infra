package com.focela.platform.module.infra.controller.admin.job.dto.job;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Admin - scheduled job page Request VO")
@Data
public class JobPageRequest extends PageParam {

    @Schema(description = "job name, fuzzy match", example = "test job")
    private String name;

    @Schema(description = "job status, see JobStatusEnum enum", example = "1")
    private Integer status;

    @Schema(description = "Handler name (fuzzy match)", example = "sysUserSessionTimeoutJob")
    private String handlerName;

}

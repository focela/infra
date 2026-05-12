package com.focela.platform.module.system.controller.admin.notice.dto;

import com.focela.platform.framework.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Admin - notice page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticePageRequest extends PageParam {

    @Schema(description = "Notice name (fuzzy match)", example = "Acme")
    private String title;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

}

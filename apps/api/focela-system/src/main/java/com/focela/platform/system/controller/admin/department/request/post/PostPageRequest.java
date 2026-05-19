package com.focela.platform.system.controller.admin.department.request.post;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Admin - post page Request")
@Data
@EqualsAndHashCode(callSuper = true)
public class PostPageRequest extends PageParam {

    @Schema(description = "post code, fuzzy match", example = "focela")
    private String code;

    @Schema(description = "post name, fuzzy match", example = "Acme")
    private String name;

    @Schema(description = "Display status, see CommonStatusEnum", example = "1")
    private Integer status;

}

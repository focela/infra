package com.focela.platform.module.system.controller.admin.oauth2.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.focela.platform.framework.common.pojo.PageParam;

@Schema(description = "管理后台 - OAuth2 客户端分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientPageRequest extends PageParam {

    @Schema(description = "应用名，模糊匹配", example = "土豆")
    private String name;

    @Schema(description = "状态，参见 CommonStatusEnum 枚举", example = "1")
    private Integer status;

}

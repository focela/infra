package com.focela.platform.system.controller.admin.oauth2.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import com.focela.platform.common.model.PageParam;

@Schema(description = "Admin - OAuth2 client page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OAuth2ClientPageRequest extends PageParam {

    @Schema(description = "application name, fuzzy match", example = "potato")
    private String name;

    @Schema(description = "Status, see CommonStatusEnum", example = "1")
    private Integer status;

}

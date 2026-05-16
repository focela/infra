package com.focela.platform.system.controller.admin.social.dto.client;

import com.focela.platform.common.model.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "Admin - social client page Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SocialClientPageRequest extends PageParam {

    @Schema(description = "Application name", example = "focelamall")
    private String name;

    @Schema(description = "Social platform type", example = "31")
    private Integer socialType;

    @Schema(description = "User type", example = "2")
    private Integer userType;

    @Schema(description = "Client ID", example = "145442115")
    private String clientId;

    @Schema(description = "Status", example = "1")
    private Integer status;

}

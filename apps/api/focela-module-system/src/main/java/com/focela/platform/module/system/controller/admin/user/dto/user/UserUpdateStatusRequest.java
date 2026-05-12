package com.focela.platform.module.system.controller.admin.user.dto.user;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.framework.dictionary.validation.InDictionary;
import com.focela.platform.module.system.enums.DictionaryTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - user update status Request VO")
@Data
public class UserUpdateStatusRequest {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "角色编号不能为空")
    private Long id;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    @InDictionary(type = DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

}

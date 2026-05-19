package com.focela.platform.system.controller.admin.user.request;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.validation.InEnum;
import com.focela.platform.dictionary.validation.InDictionary;
import com.focela.platform.system.constants.DictionaryTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Admin - user update status Request VO")
@Data
public class UserUpdateStatusRequest {

    @Schema(description = "User ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "role ID must not be blank")
    private Long id;

    @Schema(description = "Status, see CommonStatusEnum", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "update status must be {value}")
    @InDictionary(type = DictionaryTypeConstants.COMMON_STATUS)
    private Integer status;

}

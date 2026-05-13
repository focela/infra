package com.focela.platform.module.system.api.notify.dto;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.validation.InEnum;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class NotifyTemplateRpcRequest {

    @NotEmpty(message = "template name must not be blank")
    private String name;

    @NotNull(message = "template code must not be blank")
    private String code;

    @NotNull(message = "template type must not be blank")
    private Integer type;

    @NotEmpty(message = "sender name must not be blank")
    private String nickname;

    @NotEmpty(message = "template content must not be blank")
    private String content;

    @NotNull(message = "status must not be blank")
    @InEnum(value = CommonStatusEnum.class, message = "status must be {value}")
    private Integer status;

    private String remark;

}

package com.focela.platform.module.system.api.social.dto;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.enums.social.SocialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * 取消绑定社交用户 Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindReqDTO {

    /**
     * 用户编号
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * 用户类型
     */
    @InEnum(UserTypeEnum.class)
    @NotNull(message = "user type must not be blank")
    private Integer userType;

    /**
     * 社交平台的类型
     */
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "social platform type must not be blank")
    private Integer socialType;
    /**
     * 授权码
     */
    @NotEmpty(message = "authorization code must not be blank")
    private String code;
    /**
     * state
     */
    @NotNull(message = "state must not be blank")
    private String state;

}

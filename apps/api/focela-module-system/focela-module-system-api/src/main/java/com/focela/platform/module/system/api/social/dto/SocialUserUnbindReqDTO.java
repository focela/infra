package com.focela.platform.module.system.api.social.dto;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.validation.InEnum;
import com.focela.platform.module.system.api.social.enums.SocialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

/**
 * 社交绑定 Request DTO，使用 code 授权码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserUnbindReqDTO {

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
     * 社交平台的 openid
     */
    @NotEmpty(message = "social platform openid must not be blank")
    private String openid;

}

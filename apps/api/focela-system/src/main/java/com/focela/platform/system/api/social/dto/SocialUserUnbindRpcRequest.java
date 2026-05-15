package com.focela.platform.system.api.social.dto;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.validation.InEnum;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

/**
 * Social unbind Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserUnbindRpcRequest {

    /**
     * User ID
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * User type
     */
    @InEnum(UserTypeEnum.class)
    @NotNull(message = "user type must not be blank")
    private Integer userType;

    /**
     * Social platform type
     */
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "social platform type must not be blank")
    private Integer socialType;

    /**
     * Social platform openid
     */
    @NotEmpty(message = "social platform openid must not be blank")
    private String openid;

}

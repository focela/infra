package com.focela.platform.system.api.social.dto;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.validation.InEnum;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Bind social user Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindRpcRequest {

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
     * Authorization code
     */
    @NotEmpty(message = "authorization code must not be blank")
    private String code;
    /**
     * state
     */
    @NotNull(message = "state must not be blank")
    private String state;

}

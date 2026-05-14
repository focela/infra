package com.focela.platform.framework.common.contract.system.oauth2.dto;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.validation.InEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * OAuth2.0 access token create Request DTO.
 */
@Data
public class OAuth2AccessTokenCreateRpcRequest implements Serializable {

    /**
     * User ID.
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * User type.
     */
    @NotNull(message = "user type must not be blank")
    @InEnum(value = UserTypeEnum.class, message = "user type must be {value}")
    private Integer userType;
    /**
     * Client ID.
     */
    @NotNull(message = "client ID must not be blank")
    private String clientId;
    /**
     * Authorization scopes.
     */
    private List<String> scopes;

}

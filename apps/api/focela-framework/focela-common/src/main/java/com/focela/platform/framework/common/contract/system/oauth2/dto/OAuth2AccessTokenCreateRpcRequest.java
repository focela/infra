package com.focela.platform.framework.common.contract.system.oauth2.dto;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.validation.InEnum;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * OAuth2.0 访问令牌创建 Request DTO
 */
@Data
public class OAuth2AccessTokenCreateRpcRequest implements Serializable {

    /**
     * 用户编号
     */
    @NotNull(message = "user ID must not be blank")
    private Long userId;
    /**
     * 用户类型
     */
    @NotNull(message = "user type must not be blank")
    @InEnum(value = UserTypeEnum.class, message = "user type must be {value}")
    private Integer userType;
    /**
     * 客户端编号
     */
    @NotNull(message = "client ID must not be blank")
    private String clientId;
    /**
     * 授权范围
     */
    private List<String> scopes;

}

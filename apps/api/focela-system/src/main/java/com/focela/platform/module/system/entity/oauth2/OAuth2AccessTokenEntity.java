package com.focela.platform.module.system.entity.oauth2;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.tenant.core.db.TenantBaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 access token DO
 *
 * The following fields are temporarily unused and unsupported:
 * user_name, authentication (user info)
 */
@TableName(value = "system_oauth2_access_token", autoResultMap = true)
@KeySequence("system_oauth2_access_token_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2AccessTokenEntity extends TenantBaseEntity {

    /**
     * ID, database auto-increment
     */
    @TableId
    private Long id;
    /**
     * Access token
     */
    private String accessToken;
    /**
     * Refresh token
     */
    private String refreshToken;
    /**
     * User ID
     */
    private Long userId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * User info
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, String> userInfo;
    /**
     * Client ID
     *
     * Associated with {@link OAuth2ClientEntity#getId()}
     */
    private String clientId;
    /**
     * Authorization scopes
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scopes;
    /**
     * Expiration time
     */
    private LocalDateTime expiresTime;

}

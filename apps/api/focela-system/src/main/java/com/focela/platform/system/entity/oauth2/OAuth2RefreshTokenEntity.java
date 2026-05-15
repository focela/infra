package com.focela.platform.system.entity.oauth2;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.tenant.core.db.TenantBaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 refresh token
 */
@TableName(value = "system_oauth2_refresh_token", autoResultMap = true)
// Since Oracle SEQ name length is limited, we use system_oauth2_access_token_seq for now (no real issue)
@KeySequence("system_oauth2_access_token_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
public class OAuth2RefreshTokenEntity extends TenantBaseEntity {

    /**
     * ID, database auto-increment
     */
    private Long id;
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

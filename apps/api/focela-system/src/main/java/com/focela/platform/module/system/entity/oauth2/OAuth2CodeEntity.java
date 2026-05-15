package com.focela.platform.module.system.entity.oauth2;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OAuth2 authorization code DO
 */
@TableName(value = "system_oauth2_code", autoResultMap = true)
@KeySequence("system_oauth2_code_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2CodeEntity extends BaseEntity {

    /**
     * ID, database auto-increment
     */
    private Long id;
    /**
     * Authorization code
     */
    private String code;
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
     * Associated with {@link OAuth2ClientEntity#getClientId()}
     */
    private String clientId;
    /**
     * Authorization scopes
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scopes;
    /**
     * Redirect URI
     */
    private String redirectUri;
    /**
     * State
     */
    private String state;
    /**
     * Expiration time
     */
    private LocalDateTime expiresTime;

}

package com.focela.platform.system.domain.entity.oauth2;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.enums.oauth2.OAuth2GrantTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * OAuth2 client Entity
 */
@TableName(value = "system_oauth2_client", autoResultMap = true)
@KeySequence("system_oauth2_client_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class OAuth2ClientEntity extends BaseEntity {

    /**
     * ID, database auto-increment
     *
     * Since SQL Server has issues storing String primary keys, we use Long type for now
     */
    @TableId
    private Long id;
    /**
     * Client ID
     */
    private String clientId;
    /**
     * Client secret
     */
    private String secret;
    /**
     * Application name
     */
    private String name;
    /**
     * Application icon
     */
    private String logo;
    /**
     * Application description
     */
    private String description;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Access token validity (seconds)
     */
    private Integer accessTokenValiditySeconds;
    /**
     * Refresh token validity (seconds)
     */
    private Integer refreshTokenValiditySeconds;
    /**
     * Allowed redirect URIs
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> redirectUris;
    /**
     * Authorized grant types (modes)
     *
     * Enum {@link OAuth2GrantTypeEnum}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> authorizedGrantTypes;
    /**
     * Authorization scopes
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> scopes;
    /**
     * Auto-approved scopes
     *
     * During code authorization, scopes within this range are automatically approved
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> autoApproveScopes;
    /**
     * Authorities
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> authorities;
    /**
     * Resources
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> resourceIds;
    /**
     * Additional information, in JSON format
     */
    private String additionalInformation;

}

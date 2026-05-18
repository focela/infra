package com.focela.platform.system.entity.social;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.tenant.core.db.TenantBaseEntity;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.zhyd.oauth.config.AuthConfig;

/**
 * Social client Entity
 *
 * Corresponds to {@link AuthConfig}; allows each tenant to have its own client configuration for social (third-party) login.
 */
@TableName(value = "system_social_client", autoResultMap = true)
@KeySequence("system_social_client_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialClientEntity extends TenantBaseEntity {

    /**
     * ID, auto-increment
     */
    @TableId
    private Long id;
    /**
     * Application name
     */
    private String name;
    /**
     * Social type
     *
     * Enum {@link SocialTypeEnum}
     */
    private Integer socialType;
    /**
     * User type
     *
     * Purpose: different user types correspond to different mini-programs and need their own configuration.
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * Client ID
     */
    private String clientId;
    /**
     * Client secret
     */
    private String clientSecret;

    /**
     * Agent ID
     *
     * Currently used by only some "social types":
     * 1. WeCom: corresponds to the authorizing party's web application ID
     */
    private String agentId;

    /**
     * publicKey
     *
     * Currently used by only some "social types":
     * 1. Alipay: the Alipay public key
     */
    private String publicKey;

}

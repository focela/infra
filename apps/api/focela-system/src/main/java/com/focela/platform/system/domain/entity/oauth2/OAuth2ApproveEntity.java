package com.focela.platform.system.domain.entity.oauth2;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * OAuth2 approve Entity
 *
 * Records the list of scopes the user accepted on the sso.vue screen
 */
@TableName(value = "system_oauth2_approve", autoResultMap = true)
@KeySequence("system_oauth2_approve_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class OAuth2ApproveEntity extends BaseEntity {

    /**
     * ID, database auto-increment
     */
    @TableId
    private Long id;
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
     * Authorization scope
     */
    private String scope;
    /**
     * Whether accepted
     *
     * true - accepted
     * false - rejected
     */
    private Boolean approved;
    /**
     * Expiration time
     */
    private LocalDateTime expiresTime;

}

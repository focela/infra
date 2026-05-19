package com.focela.platform.system.domain.entity.logger;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.system.enums.logger.LoginLogTypeEnum;
import com.focela.platform.system.enums.logger.LoginResultEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Login log table
 *
 * Note: includes both login and logout actions
 */
@TableName("system_login_log")
@KeySequence("system_login_log_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoginLogEntity extends BaseEntity {

    /**
     * Log primary key
     */
    private Long id;
    /**
     * Log type
     *
     * Enum {@link LoginLogTypeEnum}
     */
    private Integer logType;
    /**
     * Trace ID
     */
    private String traceId;
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
     * Username
     *
     * Redundant, since accounts can change
     */
    private String username;
    /**
     * Login result
     *
     * Enum {@link LoginResultEnum}
     */
    private Integer result;
    /**
     * User IP
     */
    private String userIp;
    /**
     * Browser UA
     */
    private String userAgent;

}

package com.focela.platform.module.system.entity.user;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.tenant.core.db.TenantBaseEntity;
import com.focela.platform.module.system.enums.common.SexEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Admin user DO
 */
@TableName(value = "system_users", autoResultMap = true) // since system_user is a SQL Server keyword, use system_users
@KeySequence("system_users_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends TenantBaseEntity {

    /**
     * User ID
     */
    @TableId
    private Long id;
    /**
     * Username
     */
    private String username;
    /**
     * Encrypted password
     *
     * Since {@link BCryptPasswordEncoder} is currently used as the encoder, no manual salt handling is needed.
     */
    private String password;
    /**
     * User nickname
     */
    private String nickname;
    /**
     * Remarks
     */
    private String remark;
    /**
     * Department ID
     */
    private Long deptId;
    /**
     * Post ID array
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Set<Long> postIds;
    /**
     * User email
     */
    private String email;
    /**
     * Mobile number
     */
    private String mobile;
    /**
     * User gender
     *
     * Enum {@link SexEnum}
     */
    private Integer sex;
    /**
     * User avatar
     */
    private String avatar;
    /**
     * Account status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Last login IP
     */
    private String loginIp;
    /**
     * Last login time
     */
    private LocalDateTime loginDate;

}

package com.focela.platform.system.entity.mail;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Mail account Entity
 *
 * Purpose: configure the account used to send mail
 *
 * @since 2022-03-21
 */
@TableName(value = "system_mail_account", autoResultMap = true)
@KeySequence("system_mail_account_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class MailAccountEntity extends BaseEntity {

    /**
     * Primary key
     */
    @TableId
    private Long id;
    /**
     * Email address
     */
    private String mail;

    /**
     * Username
     */
    private String username;
    /**
     * Password
     */
    private String password;
    /**
     * SMTP server host
     */
    private String host;
    /**
     * SMTP server port
     */
    private Integer port;
    /**
     * Whether SSL is enabled
     */
    private Boolean sslEnable;
    /**
     * Whether STARTTLS is enabled
     */
    private Boolean starttlsEnable;

}

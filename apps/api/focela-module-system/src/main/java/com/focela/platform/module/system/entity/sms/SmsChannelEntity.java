package com.focela.platform.module.system.entity.sms;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.module.system.config.sms.core.enums.SmsChannelEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * SMS channel DO
 *
 * @since 2021-01-25
 */
@TableName(value = "system_sms_channel", autoResultMap = true)
@KeySequence("system_sms_channel_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TenantIgnore
public class SmsChannelEntity extends BaseEntity {

    /**
     * Channel ID
     */
    private Long id;
    /**
     * SMS signature
     */
    private String signature;
    /**
     * Channel code
     *
     * Enum {@link SmsChannelEnum}
     */
    private String code;
    /**
     * Enabled status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Remarks
     */
    private String remark;
    /**
     * SMS API account
     */
    private String apiKey;
    /**
     * SMS API secret key
     */
    private String apiSecret;
    /**
     * SMS send callback URL
     */
    private String callbackUrl;

}

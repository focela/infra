package com.focela.platform.system.domain.entity.sms;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mobile verification code Entity
 *
 * idx_mobile index: based on the {@link #mobile} field
 */
@TableName("system_sms_code")
@KeySequence("system_sms_code_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class SmsCodeEntity extends BaseEntity {

    /**
     * ID
     */
    private Long id;
    /**
     * Mobile number
     */
    private String mobile;
    /**
     * Verification code
     */
    private String code;
    /**
     * Send scene
     *
     * Enum {@link SmsCodeEntity}
     */
    private Integer scene;
    /**
     * Creation IP
     */
    private String createIp;
    /**
     * Index of the message among today's sends
     */
    private Integer todayIndex;
    /**
     * Whether used
     */
    private Boolean used;
    /**
     * Used time
     */
    private LocalDateTime usedTime;
    /**
     * Used IP
     */
    private String usedIp;

}

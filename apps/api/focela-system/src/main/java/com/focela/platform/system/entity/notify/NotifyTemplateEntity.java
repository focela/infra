package com.focela.platform.system.entity.notify;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * In-site notification template Entity
 */
@TableName(value = "system_notify_template", autoResultMap = true)
@KeySequence("system_notify_template_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class NotifyTemplateEntity extends BaseEntity {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * Template name
     */
    private String name;
    /**
     * Template code
     */
    private String code;
    /**
     * Template type
     *
     * Corresponds to system_notify_template_type dictionary
     */
    private Integer type;
    /**
     * Sender nickname
     */
    private String nickname;
    /**
     * Template content
     */
    private String content;
    /**
     * Parameter array
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Remarks
     */
    private String remark;

}

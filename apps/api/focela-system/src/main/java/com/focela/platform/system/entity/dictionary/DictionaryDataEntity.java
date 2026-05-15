package com.focela.platform.system.entity.dictionary;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Dictionary data table
 */
@TableName("system_dict_data")
@KeySequence("system_dict_data_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class DictionaryDataEntity extends BaseEntity {

    /**
     * Dictionary data ID
     */
    @TableId
    private Long id;
    /**
     * Dictionary sort order
     */
    private Integer sort;
    /**
     * Dictionary label
     */
    private String label;
    /**
     * Dictionary value
     */
    private String value;
    /**
     * Dictionary type
     *
     * Redundant {@link DictionaryDataEntity#getDictType()}
     */
    private String dictType;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Color type
     *
     * Corresponds to element-ui: default, primary, success, info, warning, danger
     */
    private String colorType;
    /**
     * CSS style
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String cssClass;
    /**
     * Remarks
     */
    private String remark;

}

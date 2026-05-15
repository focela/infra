package com.focela.platform.system.entity.department;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Post table
 */
@TableName("system_post")
@KeySequence("system_post_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class PostEntity extends BaseEntity {

    /**
     * Post ID
     */
    @TableId
    private Long id;
    /**
     * Post name
     */
    private String name;
    /**
     * Post code
     */
    private String code;
    /**
     * Post sort order
     */
    private Integer sort;
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

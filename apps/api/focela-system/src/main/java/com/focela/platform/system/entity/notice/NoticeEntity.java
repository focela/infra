package com.focela.platform.system.entity.notice;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.system.enums.notice.NoticeTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Notice table
 */
@TableName("system_notice")
@KeySequence("system_notice_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class NoticeEntity extends BaseEntity {

    /**
     * Notice ID
     */
    private Long id;
    /**
     * Notice title
     */
    private String title;
    /**
     * Notice type
     *
     * Enum {@link NoticeTypeEnum}
     */
    private Integer type;
    /**
     * Notice content
     */
    private String content;
    /**
     * Notice status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

}

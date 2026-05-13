package com.focela.platform.module.system.entity.social;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 社交用户的绑定
 * 即 {@link SocialUserEntity} 与 UserDO 的关联表
 */
@TableName(value = "system_social_user_bind", autoResultMap = true)
@KeySequence("system_social_user_bind_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindEntity extends BaseEntity {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 关联的用户编号
     *
     * 关联 UserDO 的编号
     */
    private Long userId;
    /**
     * 用户类型
     *
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * 社交平台的用户编号
     *
     * 关联 {@link SocialUserEntity#getId()}
     */
    private Long socialUserId;
    /**
     * 社交平台的类型
     *
     * 冗余 {@link SocialUserEntity#getType()}
     */
    private Integer socialType;

}

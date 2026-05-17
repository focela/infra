package com.focela.platform.system.entity.social;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Social user binding
 * The association table between {@link SocialUserEntity} and a system user.
 */
@TableName(value = "system_social_user_bind", autoResultMap = true)
@KeySequence("system_social_user_bind_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserBindEntity extends BaseEntity {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * Associated user ID
     *
     * Associated with the bound user's ID.
     */
    private Long userId;
    /**
     * User type
     *
     * Enum {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * Social platform user ID
     *
     * Associated with {@link SocialUserEntity#getId()}
     */
    private Long socialUserId;
    /**
     * Social platform type
     *
     * Redundant {@link SocialUserEntity#getType()}
     */
    private Integer socialType;

}

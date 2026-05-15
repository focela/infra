package com.focela.platform.system.entity.department;

import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.system.entity.user.UserEntity;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User-post association
 */
@TableName("system_user_post")
@KeySequence("system_user_post_seq") // Primary key auto-increment for databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPostEntity extends BaseEntity {

    /**
     * Auto-increment primary key
     */
    @TableId
    private Long id;
    /**
     * User ID
     *
     * Associated with {@link UserEntity#getId()}
     */
    private Long userId;
    /**
     * Post ID
     *
     * Associated with {@link PostEntity#getId()}
     */
    private Long postId;

}

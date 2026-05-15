package com.focela.platform.system.entity.social;

import com.focela.platform.framework.mybatis.core.entity.BaseEntity;
import com.focela.platform.system.enums.social.SocialTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Social (third-party) user
 */
@TableName(value = "system_social_user", autoResultMap = true)
@KeySequence("system_social_user_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserEntity extends BaseEntity {

    /**
     * Auto-increment primary key
     */
    @TableId
    private Long id;
    /**
     * Social platform type
     *
     * Enum {@link SocialTypeEnum}
     */
    private Integer type;

    /**
     * Social openid
     */
    private String openid;
    /**
     * Social token
     */
    private String token;
    /**
     * Raw token data, typically in JSON format
     */
    private String rawTokenInfo;

    /**
     * User nickname
     */
    private String nickname;
    /**
     * User avatar
     */
    private String avatar;
    /**
     * Raw user data, typically in JSON format
     */
    private String rawUserInfo;

    /**
     * Last authentication code
     */
    private String code;
    /**
     * Last authentication state
     */
    private String state;

}



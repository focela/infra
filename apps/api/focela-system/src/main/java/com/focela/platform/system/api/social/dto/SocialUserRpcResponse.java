package com.focela.platform.system.api.social.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Social user Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserRpcResponse {

    /**
     * Social user openid
     */
    private String openid;
    /**
     * Social user nickname
     */
    private String nickname;
    /**
     * Social user avatar
     */
    private String avatar;

    /**
     * Associated user ID
     */
    private Long userId;

}

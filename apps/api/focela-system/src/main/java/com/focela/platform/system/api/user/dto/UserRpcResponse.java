package com.focela.platform.system.api.user.dto;

import com.focela.platform.common.enums.CommonStatusEnum;
import lombok.Data;

import java.util.Set;

/**
 * Admin user response
 */
@Data
public class UserRpcResponse {

    /**
     * User ID
     */
    private Long id;
    /**
     * User nickname
     */
    private String nickname;
    /**
     * Account status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * Department ID
     */
    private Long deptId;
    /**
     * Post IDs
     */
    private Set<Long> postIds;
    /**
     * Mobile number
     */
    private String mobile;
    /**
     * User avatar
     */
    private String avatar;

}

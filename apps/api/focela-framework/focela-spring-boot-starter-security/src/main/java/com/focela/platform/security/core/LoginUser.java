package com.focela.platform.security.core;

import cn.hutool.core.map.MapUtil;
import com.focela.platform.common.enums.UserTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Login user information
 */
@Data
public class LoginUser {

    public static final String INFO_KEY_NICKNAME = "nickname";
    public static final String INFO_KEY_DEPT_ID = "deptId";

    /**
     * User ID
     */
    private Long id;
    /**
     * User type
     *
     * Associated with {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * Additional user information
     */
    private Map<String, String> info;
    /**
     * Tenant ID
     */
    private Long tenantId;
    /**
     * Authorization scopes
     */
    private List<String> scopes;
    /**
     * Expiration time
     */
    private LocalDateTime expiresTime;

    // ========== Context ==========
    /**
     * Context field, not persisted
     *
     * 1. Used for temporary caching keyed by LoginUser
     */
    @JsonIgnore
    private Map<String, Object> context;
    /**
     * Visited tenant ID
     */
    private Long visitTenantId;

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }

}

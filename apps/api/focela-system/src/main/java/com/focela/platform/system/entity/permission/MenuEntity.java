package com.focela.platform.system.entity.permission;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.mybatis.core.entity.BaseEntity;
import com.focela.platform.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.enums.permission.MenuTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Menu Entity
 */
@TableName("system_menu")
@KeySequence("system_menu_seq") // used for primary key auto-increment in databases such as Oracle, PostgreSQL, Kingbase, DB2, H2. Can be omitted for databases like MySQL.
@Data
@EqualsAndHashCode(callSuper = true)
@TenantIgnore
public class MenuEntity extends BaseEntity {

    /**
     * Menu ID - root node
     */
    public static final Long ID_ROOT = 0L;

    /**
     * Menu ID
     */
    @TableId
    private Long id;
    /**
     * Menu name
     */
    private String name;
    /**
     * Permission identifier
     *
     * General format: ${system}:${module}:${operation}
     * For example: system:admin:add, meaning add admin in the system service.
     *
     * When this MenuEntity is granted to a role, the role gains access to this resource:
     * - For the backend, combined with @PreAuthorize annotation to require this permission for API endpoints, enabling API permission control.
     * - For the frontend, combined with frontend tags to control whether buttons are shown, preventing users from seeing actions they lack permission for.
     */
    private String permission;
    /**
     * Menu type
     *
     * Enum {@link MenuTypeEnum}
     */
    private Integer type;
    /**
     * Display order
     */
    private Integer sort;
    /**
     * Parent menu ID
     */
    private Long parentId;
    /**
     * Route path
     *
     * If path is http(s), it is an external link.
     */
    private String path;
    /**
     * Menu icon
     */
    private String icon;
    /**
     * Component path
     */
    private String component;
    /**
     * Component name
     */
    private String componentName;
    /**
     * Status
     *
     * Enum {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * Whether visible
     *
     * Only used by menus and directories.
     * When set to true, the menu is not shown in the sidebar but the route still exists. For example, standalone edit pages like /edit/1024.
     */
    private Boolean visible;
    /**
     * Whether to cache
     *
     * Only used by menus and directories; uses Vue Router's keep-alive feature.
     * Note: if caching is enabled, {@link #componentName} must be set, otherwise caching will not work.
     */
    private Boolean keepAlive;
    /**
     * Whether always show
     *
     * If false, when the menu has only one child menu, the parent is not shown and the child is displayed directly.
     */
    private Boolean alwaysShow;

}

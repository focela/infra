package com.focela.platform.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuSaveRequest;
import com.focela.platform.system.entity.permission.MenuEntity;
import com.focela.platform.system.repository.mapper.permission.MenuMapper;
import com.focela.platform.system.constants.RedisKeyConstants;
import com.focela.platform.system.enums.permission.MenuTypeEnum;
import com.focela.platform.system.service.tenant.TenantService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertList;
import static com.focela.platform.common.utils.collection.CollectionUtils.convertMap;
import static com.focela.platform.system.entity.permission.MenuEntity.ID_ROOT;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Menu Service implementation
 */
@Service
@Slf4j
public class DefaultMenuService implements MenuService {

    @Resource
    private MenuMapper menuMapper;
    @Resource
    private PermissionService permissionService;
    @Resource
    @Lazy // Lazy to avoid circular dependency errors
    private TenantService tenantService;

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#createRequest.permission",
            condition = "#createRequest.permission != null")
    public Long createMenu(MenuSaveRequest createRequest) {
        // Validate that the parent menu exists
        validateParentMenu(createRequest.getParentId(), null);
        // Validate the menu (itself)
        validateMenuName(createRequest.getParentId(), createRequest.getName(), null);
        validateMenuComponentName(createRequest.getComponentName(), null);

        // Insert into the database
        MenuEntity menu = BeanUtils.toBean(createRequest, MenuEntity.class);
        initMenuProperty(menu);
        menuMapper.insert(menu);
        // Return
        return menu.getId();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries clears all caches because a permission change involves both old and new permissions; clearing all is simple and effective
    public void updateMenu(MenuSaveRequest updateRequest) {
        // Validate that the menu to update exists
        if (menuMapper.selectById(updateRequest.getId()) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // Validate that the parent menu exists
        validateParentMenu(updateRequest.getParentId(), updateRequest.getId());
        // Validate the menu (itself)
        validateMenuName(updateRequest.getParentId(), updateRequest.getName(), updateRequest.getId());
        validateMenuComponentName(updateRequest.getComponentName(), updateRequest.getId());

        // Update in the database
        MenuEntity updateObj = BeanUtils.toBean(updateRequest, MenuEntity.class);
        initMenuProperty(updateObj);
        menuMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries clears all caches because the permission corresponding to id is unknown here; clearing all is simple and effective
    public void deleteMenu(Long id) {
        // Validate whether there are still sub-menus
        if (menuMapper.selectCountByParentId(id) > 0) {
            throw exception(MENU_EXISTS_CHILDREN);
        }
        // Validate that the menu to delete exists
        if (menuMapper.selectById(id) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // Mark as deleted
        menuMapper.deleteById(id);
        // Remove the permission granted to roles
        permissionService.processMenuDeleted(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST,
            allEntries = true) // allEntries clears all caches because Spring Cache does not support batch deletion by ids
    public void deleteMenuList(List<Long> ids) {
        // Validate whether there are still sub-menus
        ids.forEach(id -> {
            if (menuMapper.selectCountByParentId(id) > 0) {
                throw exception(MENU_EXISTS_CHILDREN);
            }
        });

        // Mark as deleted
        menuMapper.deleteByIds(ids);
        // Remove the permission granted to roles
        ids.forEach(id -> permissionService.processMenuDeleted(id));
    }

    @Override
    public List<MenuEntity> getMenuList() {
        return menuMapper.selectList();
    }

    @Override
    public List<MenuEntity> getMenuListByTenant(MenuListRequest request) {
        // Query all menus and filter out disabled nodes
        List<MenuEntity> menus = getMenuList(request);
        // When multi-tenancy is enabled, filter out menus not granted to the tenant
        tenantService.handleTenantMenu(menuIds -> menus.removeIf(menu -> !CollUtil.contains(menuIds, menu.getId())));
        return menus;
    }

    @Override
    public List<MenuEntity> filterDisableMenus(List<MenuEntity> menuList) {
        if (CollUtil.isEmpty(menuList)){
            return Collections.emptyList();
        }
        Map<Long, MenuEntity> menuMap = convertMap(menuList, MenuEntity::getId);

        // Iterate menus, find the non-disabled ones, and add them to enabledMenus
        List<MenuEntity> enabledMenus = new ArrayList<>();
        Set<Long> disabledMenuCache = new HashSet<>(); // remember menus already determined disabled to avoid repeated searches
        for (MenuEntity menu : menuList) {
            if (isMenuDisabled(menu, menuMap, disabledMenuCache)) {
                continue;
            }
            enabledMenus.add(menu);
        }
        return enabledMenus;
    }

    private boolean isMenuDisabled(MenuEntity node, Map<Long, MenuEntity> menuMap, Set<Long> disabledMenuCache) {
        // If already determined as disabled, return immediately
        if (disabledMenuCache.contains(node.getId())) {
            return true;
        }

        // 1. Check whether the node itself is disabled
        if (CommonStatusEnum.isDisable(node.getStatus())) {
            disabledMenuCache.add(node.getId());
            return true;
        }

        // 2. When parentId reaches the root node, no further check is needed
        Long parentId = node.getParentId();
        if (ObjUtil.equal(parentId, ID_ROOT)) {
            return false;
        }

        // 3. Continue traversing the parent node
        MenuEntity parent = menuMap.get(parentId);
        if (parent == null || isMenuDisabled(parent, menuMap, disabledMenuCache)) {
            disabledMenuCache.add(node.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<MenuEntity> getMenuList(MenuListRequest request) {
        return menuMapper.selectList(request);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, key = "#permission")
    public List<Long> getMenuIdListByPermissionFromCache(String permission) {
        List<MenuEntity> menus = menuMapper.selectListByPermission(permission);
        return convertList(menus, MenuEntity::getId);
    }

    @Override
    public MenuEntity getMenu(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public List<MenuEntity> getMenuList(Collection<Long> ids) {
        // When ids is empty, return an empty list
        if (CollUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return menuMapper.selectByIds(ids);
    }

    /**
     * Validate whether the parent menu is valid
     * <p>
     * 1. Cannot set itself as the parent menu
     * 2. The parent menu does not exist
     * 3. The parent menu must be of {@link MenuTypeEnum#MENU} type
     *
     * @param parentId parent menu ID
     * @param childId  current menu ID
     */
    @VisibleForTesting
    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || ID_ROOT.equals(parentId)) {
            return;
        }
        // Cannot set itself as the parent menu
        if (parentId.equals(childId)) {
            throw exception(MENU_PARENT_ERROR);
        }
        MenuEntity menu = menuMapper.selectById(parentId);
        // Parent menu does not exist
        if (menu == null) {
            throw exception(MENU_PARENT_NOT_EXISTS);
        }
        // The parent menu must be of dir or menu type
        if (!MenuTypeEnum.DIR.getType().equals(menu.getType())
                && !MenuTypeEnum.MENU.getType().equals(menu.getType())) {
            throw exception(MENU_PARENT_NOT_DIR_OR_MENU);
        }
    }

    /**
     * Validate whether the menu is valid
     * <p>
     * 1. Validate whether the same menu name exists under the same parent menu ID
     *
     * @param name     menu name
     * @param parentId parent menu ID
     * @param id       menu ID
     */
    @VisibleForTesting
    void validateMenuName(Long parentId, String name, Long id) {
        MenuEntity menu = menuMapper.selectByParentIdAndName(parentId, name);
        if (menu == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same menu id
        if (id == null) {
            throw exception(MENU_NAME_DUPLICATE);
        }
        if (!menu.getId().equals(id)) {
            throw exception(MENU_NAME_DUPLICATE);
        }
    }

    /**
     * Validate whether the menu component name is valid
     *
     * @param componentName component name
     * @param id            menu ID
     */
    @VisibleForTesting
    void validateMenuComponentName(String componentName, Long id) {
        if (StrUtil.isBlank(componentName)) {
            return;
        }
        MenuEntity menu = menuMapper.selectByComponentName(componentName);
        if (menu == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same menu id
        if (id == null) {
            throw exception(MENU_COMPONENT_NAME_DUPLICATE);
        }
        if (!menu.getId().equals(id)) {
            throw exception(MENU_COMPONENT_NAME_DUPLICATE);
        }
    }

    /**
     * Initialize common menu properties.
     * <p>
     * For example, only dir or menu types should have an icon set.
     *
     * @param menu menu
     */
    private void initMenuProperty(MenuEntity menu) {
        // When the menu is a button type, clear out component, icon and path
        if (MenuTypeEnum.BUTTON.getType().equals(menu.getType())) {
            menu.setComponent("");
            menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }
    }

}

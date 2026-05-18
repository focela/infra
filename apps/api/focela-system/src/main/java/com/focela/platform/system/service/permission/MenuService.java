package com.focela.platform.system.service.permission;

import com.focela.platform.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuSaveRequest;
import com.focela.platform.system.entity.permission.MenuEntity;

import java.util.Collection;
import java.util.List;

/**
 * Menu Service interface
 */
public interface MenuService {

    /**
     * Create a menu
     *
     * @param createRequest menu info
     * @return ID of the created menu
     */
    Long createMenu(MenuSaveRequest createRequest);

    /**
     * Update a menu
     *
     * @param updateRequest menu info
     */
    void updateMenu(MenuSaveRequest updateRequest);

    /**
     * Delete a menu
     *
     * @param id menu ID
     */
    void deleteMenu(Long id);

    /**
     * Batch delete menus
     *
     * @param ids menu ID array
     */
    void deleteMenuList(List<Long> ids);

    /**
     * Get the list of all menus
     *
     * @return menu list
     */
    List<MenuEntity> getMenuList();

    /**
     * Filter menu list by tenant.
     * Note: for the system tenant, the full menu list is returned.
     *
     * @param request filter conditions
     * @return menu list
     */
    List<MenuEntity> getMenuListByTenant(MenuListRequest request);

    /**
     * Filter out disabled menus and their sub-menus
     *
     * @param list menu list
     * @return filtered menu list
     */
    List<MenuEntity> filterDisableMenus(List<MenuEntity> list);

    /**
     * Filter menu list
     *
     * @param request filter conditions
     * @return menu list
     */
    List<MenuEntity> getMenuList(MenuListRequest request);

    /**
     * Get the menu ID array corresponding to a permission
     *
     * @param permission permission identifier
     * @return array
     */
    List<Long> getMenuIdListByPermissionFromCache(String permission);

    /**
     * Get a menu
     *
     * @param id menu ID
     * @return menu
     */
    MenuEntity getMenu(Long id);

    /**
     * Get menu array
     *
     * @param ids menu ID array
     * @return menu array
     */
    List<MenuEntity> getMenuList(Collection<Long> ids);

}

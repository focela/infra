package com.focela.platform.module.system.service.permission;

import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuSaveRequest;
import com.focela.platform.module.system.repository.entity.permission.MenuEntity;

import java.util.Collection;
import java.util.List;

/**
 * 菜单 Service 接口
 *
 * @author 芋道源码
 */
public interface MenuService {

    /**
     * 创建菜单
     *
     * @param createRequest 菜单信息
     * @return 创建出来的菜单编号
     */
    Long createMenu(MenuSaveRequest createRequest);

    /**
     * 更新菜单
     *
     * @param updateRequest 菜单信息
     */
    void updateMenu(MenuSaveRequest updateRequest);

    /**
     * 删除菜单
     *
     * @param id 菜单编号
     */
    void deleteMenu(Long id);

    /**
     * 批量删除菜单
     *
     * @param ids 菜单编号数组
     */
    void deleteMenuList(List<Long> ids);

    /**
     * 获得所有菜单列表
     *
     * @return 菜单列表
     */
    List<MenuEntity> getMenuList();

    /**
     * 基于租户，筛选菜单列表
     * 注意，如果是系统租户，返回的还是全菜单
     *
     * @param request 筛选条件请求 VO
     * @return 菜单列表
     */
    List<MenuEntity> getMenuListByTenant(MenuListRequest request);

    /**
     * 过滤掉关闭的菜单及其子菜单
     *
     * @param list 菜单列表
     * @return 过滤后的菜单列表
     */
    List<MenuEntity> filterDisableMenus(List<MenuEntity> list);

    /**
     * 筛选菜单列表
     *
     * @param request 筛选条件请求 VO
     * @return 菜单列表
     */
    List<MenuEntity> getMenuList(MenuListRequest request);

    /**
     * 获得权限对应的菜单编号数组
     *
     * @param permission 权限标识
     * @return 数组
     */
    List<Long> getMenuIdListByPermissionFromCache(String permission);

    /**
     * 获得菜单
     *
     * @param id 菜单编号
     * @return 菜单
     */
    MenuEntity getMenu(Long id);

    /**
     * 获得菜单数组
     *
     * @param ids 菜单编号数组
     * @return 菜单数组
     */
    List<MenuEntity> getMenuList(Collection<Long> ids);

}

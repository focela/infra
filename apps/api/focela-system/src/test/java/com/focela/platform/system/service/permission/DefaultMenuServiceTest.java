package com.focela.platform.system.service.permission;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.controller.admin.permission.request.menu.MenuListRequest;
import com.focela.platform.system.controller.admin.permission.request.menu.MenuSaveRequest;
import com.focela.platform.system.domain.entity.permission.MenuEntity;
import com.focela.platform.system.repository.mapper.permission.MenuMapper;
import com.focela.platform.system.enums.permission.MenuTypeEnum;
import com.focela.platform.system.service.tenant.TenantService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.focela.platform.common.utils.collection.SetUtils.asSet;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static com.focela.platform.system.domain.entity.permission.MenuEntity.ID_ROOT;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@Import(DefaultMenuService.class)
public class DefaultMenuServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultMenuService menuService;

    @Resource
    private MenuMapper menuMapper;

    @MockitoBean
    private PermissionService permissionService;
    @MockitoBean
    private TenantService tenantService;

    @Test
    public void testCreateMenu_success() {
        // mock data（build parent menu）
        MenuEntity menuEntity = buildMenuEntity(MenuTypeEnum.MENU,
                "parent", 0L);
        menuMapper.insert(menuEntity);
        Long parentId = menuEntity.getId();
        // prepare parameters
        MenuSaveRequest request = randomPojo(MenuSaveRequest.class, o -> {
            o.setParentId(parentId);
            o.setName("testSonName");
            o.setType(MenuTypeEnum.MENU.getType());
        }).setId(null); // prevent id from being assigned
        Long menuId = menuService.createMenu(request);

        // verify record properties are correct
        MenuEntity dbMenu = menuMapper.selectById(menuId);
        assertPojoEquals(request, dbMenu, "id");
    }

    @Test
    public void testUpdateMenu_success() {
        // mock data（build parent and child menus）
        MenuEntity sonMenuEntity = createParentAndSonMenu();
        Long sonId = sonMenuEntity.getId();
        // prepare parameters
        MenuSaveRequest request = randomPojo(MenuSaveRequest.class, o -> {
            o.setId(sonId);
            o.setName("testSonName"); // modify name
            o.setParentId(sonMenuEntity.getParentId());
            o.setType(MenuTypeEnum.MENU.getType());
        });

        // invoke
        menuService.updateMenu(request);
        // verify record properties are correct
        MenuEntity dbMenu = menuMapper.selectById(sonId);
        assertPojoEquals(request, dbMenu);
    }

    @Test
    public void testUpdateMenu_sonIdNotExist() {
        // prepare parameters
        MenuSaveRequest request = randomPojo(MenuSaveRequest.class);
        // invoke, and assert exception
        assertServiceException(() -> menuService.updateMenu(request), MENU_NOT_FOUND);
    }

    @Test
    public void testDeleteMenu_success() {
        // mock data
        MenuEntity menuEntity = randomPojo(MenuEntity.class);
        menuMapper.insert(menuEntity);
        // prepare parameters
        Long id = menuEntity.getId();

        // invoke
        menuService.deleteMenu(id);
        // assert
        MenuEntity dbMenuEntity = menuMapper.selectById(id);
        assertNull(dbMenuEntity);
        verify(permissionService).processMenuDeleted(id);
    }

    @Test
    public void testDeleteMenu_menuNotExist() {
        assertServiceException(() -> menuService.deleteMenu(randomLongId()),
                MENU_NOT_FOUND);
    }

    @Test
    public void testDeleteMenu_existChildren() {
        // mock data（build parent and child menus）
        MenuEntity sonMenu = createParentAndSonMenu();
        // prepare parameters
        Long parentId = sonMenu.getParentId();

        // invokeand assert exception
        assertServiceException(() -> menuService.deleteMenu(parentId), MENU_EXISTS_CHILDREN);
    }

    @Test
    public void testGetMenuList_all() {
        // mock data
        MenuEntity menu100 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu100);
        MenuEntity menu101 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu101);
        // prepare parameters

        // invoke
        List<MenuEntity> list = menuService.getMenuList();
        // assert
        assertEquals(2, list.size());
        assertPojoEquals(menu100, list.get(0));
        assertPojoEquals(menu101, list.get(1));
    }

    @Test
    public void testGetMenuList() {
        // mock data
        MenuEntity menuEntity = randomPojo(MenuEntity.class, o -> o.setName("Focela").setStatus(CommonStatusEnum.ENABLE.getStatus()));
        menuMapper.insert(menuEntity);
        // test status mismatch
        menuMapper.insert(cloneIgnoreId(menuEntity, o -> o.setStatus(CommonStatusEnum.DISABLE.getStatus())));
        // test name mismatch
        menuMapper.insert(cloneIgnoreId(menuEntity, o -> o.setName("Other")));
        // prepare parameters
        MenuListRequest request = new MenuListRequest().setName("Focela").setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        List<MenuEntity> result = menuService.getMenuList(request);
        // assert
        assertEquals(1, result.size());
        assertPojoEquals(menuEntity, result.get(0));
    }

    @Test
    public void testGetMenuListByTenant() {
        // mock data
        MenuEntity menu100 = randomPojo(MenuEntity.class, o -> o.setId(100L).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        menuMapper.insert(menu100);
        MenuEntity menu101 = randomPojo(MenuEntity.class, o -> o.setId(101L).setStatus(CommonStatusEnum.DISABLE.getStatus()));
        menuMapper.insert(menu101);
        MenuEntity menu102 = randomPojo(MenuEntity.class, o -> o.setId(102L).setStatus(CommonStatusEnum.ENABLE.getStatus()));
        menuMapper.insert(menu102);
        // mock filter menu
        Set<Long> menuIds = asSet(100L, 101L);
        doNothing().when(tenantService).handleTenantMenu(argThat(handler -> {
            handler.handle(menuIds);
            return true;
        }));
        // prepare parameters
        MenuListRequest request = new MenuListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus());

        // invoke
        List<MenuEntity> result = menuService.getMenuListByTenant(request);
        // assert
        assertEquals(1, result.size());
        assertPojoEquals(menu100, result.get(0));
    }

    @Test
    public void testGetMenuIdListByPermissionFromCache() {
        // mock data
        MenuEntity menu100 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu100);
        MenuEntity menu101 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu101);
        // prepare parameters
        String permission = menu100.getPermission();

        // invoke
        List<Long> ids = menuService.getMenuIdListByPermissionFromCache(permission);
        // assert
        assertEquals(1, ids.size());
        assertEquals(menu100.getId(), ids.get(0));
    }

    @Test
    public void testGetMenuList_ids() {
        // mock data
        MenuEntity menu100 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu100);
        MenuEntity menu101 = randomPojo(MenuEntity.class);
        menuMapper.insert(menu101);
        // prepare parameters
        Collection<Long> ids = Collections.singleton(menu100.getId());

        // invoke
        List<MenuEntity> list = menuService.getMenuList(ids);
        // assert
        assertEquals(1, list.size());
        assertPojoEquals(menu100, list.get(0));
    }

    @Test
    public void testGetMenu() {
        // mock data
        MenuEntity menu = randomPojo(MenuEntity.class);
        menuMapper.insert(menu);
        // prepare parameters
        Long id = menu.getId();

        // invoke
        MenuEntity dbMenu = menuService.getMenu(id);
        // assert
        assertPojoEquals(menu, dbMenu);
    }

    @Test
    public void testValidateParentMenu_success() {
        // mock data
        MenuEntity menuEntity = buildMenuEntity(MenuTypeEnum.MENU, "parent", 0L);
        menuMapper.insert(menuEntity);
        // prepare parameters
        Long parentId = menuEntity.getId();

        // invoke, no assertion needed
        menuService.validateParentMenu(parentId, null);
    }

    @Test
    public void testValidateParentMenu_cannotSetSelfToBeParent() {
        // invoke, and assert exception
        assertServiceException(() -> menuService.validateParentMenu(1L, 1L),
                MENU_PARENT_ERROR);
    }

    @Test
    public void testValidateParentMenu_parentNotExist() {
        // invoke, and assert exception
        assertServiceException(() -> menuService.validateParentMenu(randomLongId(), null),
                MENU_PARENT_NOT_FOUND);
    }

    @Test
    public void testValidateParentMenu_parentTypeError() {
        // mock data
        MenuEntity menuEntity = buildMenuEntity(MenuTypeEnum.BUTTON, "parent", 0L);
        menuMapper.insert(menuEntity);
        // prepare parameters
        Long parentId = menuEntity.getId();

        // invoke, and assert exception
        assertServiceException(() -> menuService.validateParentMenu(parentId, null),
                MENU_PARENT_NOT_DIR_OR_MENU);
    }

    @Test
    public void testValidateMenu_Name_success() {
        // mock parent/child menu
        MenuEntity sonMenu = createParentAndSonMenu();
        // prepare parameters
        Long parentId = sonMenu.getParentId();
        Long otherSonMenuId = randomLongId();
        String otherSonMenuName = randomString();

        // invoke, no assertion needed
        menuService.validateMenuName(parentId, otherSonMenuName, otherSonMenuId);
    }

    @Test
    public void testValidateMenu_sonMenuNameNameDuplicate() {
        // mock parent/child menu
        MenuEntity sonMenu = createParentAndSonMenu();
        // prepare parameters
        Long parentId = sonMenu.getParentId();
        Long otherSonMenuId = randomLongId();
        String otherSonMenuName = sonMenu.getName(); //same name

        // invoke, and assert exception
        assertServiceException(() -> menuService.validateMenuName(parentId, otherSonMenuName, otherSonMenuId),
                MENU_NAME_DUPLICATE);
    }

    // ====================== initialize method ======================

    /**
     * insert parent and child menus, return child menu
     *
     * @return child menu
     */
    private MenuEntity createParentAndSonMenu() {
        // build parent and child menus
        MenuEntity parentMenuEntity = buildMenuEntity(MenuTypeEnum.MENU, "parent", ID_ROOT);
        menuMapper.insert(parentMenuEntity);
        // build child menu
        MenuEntity sonMenuEntity = buildMenuEntity(MenuTypeEnum.MENU, "testSonName",
                parentMenuEntity.getParentId());
        menuMapper.insert(sonMenuEntity);
        return sonMenuEntity;
    }

    private MenuEntity buildMenuEntity(MenuTypeEnum type, String name, Long parentId) {
        return buildMenuEntity(type, name, parentId, randomCommonStatus());
    }

    private MenuEntity buildMenuEntity(MenuTypeEnum type, String name, Long parentId, Integer status) {
        return randomPojo(MenuEntity.class, o -> o.setId(null).setName(name).setParentId(parentId)
                .setType(type.getType()).setStatus(status));
    }

}

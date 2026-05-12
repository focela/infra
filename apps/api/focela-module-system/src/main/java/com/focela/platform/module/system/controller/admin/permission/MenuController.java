package com.focela.platform.module.system.controller.admin.permission;

import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuResponse;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuSaveRequest;
import com.focela.platform.module.system.controller.admin.permission.dto.menu.MenuSimpleResponse;
import com.focela.platform.module.system.repository.entity.permission.MenuEntity;
import com.focela.platform.module.system.service.permission.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 菜单")
@RestController
@RequestMapping("/system/menu")
@Validated
public class MenuController {

    @Resource
    private MenuService menuService;

    @PostMapping("/create")
    @Operation(summary = "创建菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:create')")
    public CommonResult<Long> createMenu(@Valid @RequestBody MenuSaveRequest createRequest) {
        Long menuId = menuService.createMenu(createRequest);
        return success(menuId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public CommonResult<Boolean> updateMenu(@Valid @RequestBody MenuSaveRequest updateRequest) {
        menuService.updateMenu(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "菜单编号", required= true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public CommonResult<Boolean> deleteMenu(@RequestParam("id") Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除菜单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public CommonResult<Boolean> deleteMenuList(@RequestParam("ids") List<Long> ids) {
        menuService.deleteMenuList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取菜单列表", description = "用于【菜单管理】界面")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public CommonResult<List<MenuResponse>> getMenuList(MenuListRequest request) {
        List<MenuEntity> list = menuService.getMenuList(request);
        list.sort(Comparator.comparing(MenuEntity::getSort));
        return success(BeanUtils.toBean(list, MenuResponse.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "获取菜单精简信息列表",
            description = "只包含被开启的菜单，用于【角色分配菜单】功能的选项。在多租户的场景下，会只返回租户所在套餐有的菜单")
    public CommonResult<List<MenuSimpleResponse>> getSimpleMenuList() {
        List<MenuEntity> list = menuService.getMenuListByTenant(
                new MenuListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        list = menuService.filterDisableMenus(list);
        list.sort(Comparator.comparing(MenuEntity::getSort));
        return success(BeanUtils.toBean(list, MenuSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取菜单信息")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public CommonResult<MenuResponse> getMenu(Long id) {
        MenuEntity menu = menuService.getMenu(id);
        return success(BeanUtils.toBean(menu, MenuResponse.class));
    }

}

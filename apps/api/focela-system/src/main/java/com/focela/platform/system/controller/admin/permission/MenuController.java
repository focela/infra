package com.focela.platform.system.controller.admin.permission;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuListRequest;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuResponse;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuSaveRequest;
import com.focela.platform.system.controller.admin.permission.dto.menu.MenuSimpleResponse;
import com.focela.platform.system.entity.permission.MenuEntity;
import com.focela.platform.system.service.permission.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Menu")
@RestController
@RequestMapping("/system/menu")
@Validated
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/create")
    @Operation(summary = "create menu")
    @PreAuthorize("@ss.hasPermission('system:menu:create')")
    public CommonResult<Long> createMenu(@Valid @RequestBody MenuSaveRequest createRequest) {
        Long menuId = menuService.createMenu(createRequest);
        return success(menuId);
    }

    @PutMapping("/update")
    @Operation(summary = "update menu")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public CommonResult<Boolean> updateMenu(@Valid @RequestBody MenuSaveRequest updateRequest) {
        menuService.updateMenu(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete menu")
    @Parameter(name = "id", description = "Menu ID", required= true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public CommonResult<Boolean> deleteMenu(@RequestParam("id") Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete menu")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public CommonResult<Boolean> deleteMenuList(@RequestParam("ids") List<Long> ids) {
        menuService.deleteMenuList(ids);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "get menu list", description = "for [menu management]page")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public CommonResult<List<MenuResponse>> getMenuList(MenuListRequest request) {
        List<MenuEntity> list = menuService.getMenuList(request);
        list.sort(Comparator.comparing(MenuEntity::getSort));
        return success(BeanUtils.toBean(list, MenuResponse.class));
    }

    @GetMapping({"/list-all-simple", "simple-list"})
    @Operation(summary = "get menu simplified info list",
            description = "only include enabled menu, for [role-menu assignment]feature options. in multi-tenant scenario, return only tenant belongs to package has menu")
    public CommonResult<List<MenuSimpleResponse>> getSimpleMenuList() {
        List<MenuEntity> list = menuService.getMenuListByTenant(
                new MenuListRequest().setStatus(CommonStatusEnum.ENABLE.getStatus()));
        list = menuService.filterDisableMenus(list);
        list.sort(Comparator.comparing(MenuEntity::getSort));
        return success(BeanUtils.toBean(list, MenuSimpleResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get menu info")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public CommonResult<MenuResponse> getMenu(Long id) {
        MenuEntity menu = menuService.getMenu(id);
        return success(BeanUtils.toBean(menu, MenuResponse.class));
    }

}

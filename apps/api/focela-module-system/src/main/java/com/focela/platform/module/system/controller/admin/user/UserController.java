package com.focela.platform.module.system.controller.admin.user;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.user.dto.user.*;
import com.focela.platform.module.system.converter.user.UserConverter;
import com.focela.platform.module.system.repository.entity.department.DepartmentEntity;
import com.focela.platform.module.system.repository.entity.user.AdminUserEntity;
import com.focela.platform.module.system.enums.common.SexEnum;
import com.focela.platform.module.system.service.department.DepartmentService;
import com.focela.platform.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.common.utils.collection.CollectionUtils.convertList;

@Tag(name = "Admin - User")
@RestController
@RequestMapping("/system/user")
@Validated
public class UserController {

    @Resource
    private AdminUserService userService;
    @Resource
    private DepartmentService deptService;

    @PostMapping("/create")
    @Operation(summary = "create user")
    @PreAuthorize("@ss.hasPermission('system:user:create')")
    public CommonResult<Long> createUser(@Valid @RequestBody UserSaveRequest request) {
        Long id = userService.createUser(request);
        return success(id);
    }

    @PutMapping("update")
    @Operation(summary = "update user")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody UserSaveRequest request) {
        userService.updateUser(request);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete user")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUser(@RequestParam("id") Long id) {
        userService.deleteUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete user")
    @PreAuthorize("@ss.hasPermission('system:user:delete')")
    public CommonResult<Boolean> deleteUserList(@RequestParam("ids") List<Long> ids) {
        userService.deleteUserList(ids);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "reset user password")
    @PreAuthorize("@ss.hasPermission('system:user:update-password')")
    public CommonResult<Boolean> updateUserPassword(@Valid @RequestBody UserUpdatePasswordRequest request) {
        userService.updateUserPassword(request.getId(), request.getPassword());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "update user status")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    public CommonResult<Boolean> updateUserStatus(@Valid @RequestBody UserUpdateStatusRequest request) {
        userService.updateUserStatus(request.getId(), request.getStatus());
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "get user page list")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<PageResult<UserResponse>> getUserPage(@Valid UserPageRequest pageRequest) {
        // 获得用户分页列表
        PageResult<AdminUserEntity> pageResult = userService.getUserPage(pageRequest);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }
        // 拼接数据
        Map<Long, DepartmentEntity> deptMap = deptService.getDeptMap(
                convertList(pageResult.getList(), AdminUserEntity::getDeptId));
        return success(new PageResult<>(UserConverter.INSTANCE.convertList(pageResult.getList(), deptMap),
                pageResult.getTotal()));
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "get user simplified info list", description = "only include enabled user, for frontend dropdown options")
    public CommonResult<List<UserSimpleResponse>> getSimpleUserList() {
        List<AdminUserEntity> list = userService.getUserListByStatus(CommonStatusEnum.ENABLE.getStatus());
        // 拼接数据
        Map<Long, DepartmentEntity> deptMap = deptService.getDeptMap(
                convertList(list, AdminUserEntity::getDeptId));
        return success(UserConverter.INSTANCE.convertSimpleList(list, deptMap));
    }

    @GetMapping("/get")
    @Operation(summary = "get user detail")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:user:query')")
    public CommonResult<UserResponse> getUser(@RequestParam("id") Long id) {
        AdminUserEntity user = userService.getUser(id);
        if (user == null) {
            return success(null);
        }
        // 拼接数据
        DepartmentEntity dept = deptService.getDept(user.getDeptId());
        return success(UserConverter.INSTANCE.convert(user, dept));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export user")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserList(@Validated UserPageRequest exportRequest,
                               HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AdminUserEntity> list = userService.getUserPage(exportRequest).getList();
        // 输出 Excel
        Map<Long, DepartmentEntity> deptMap = deptService.getDeptMap(
                convertList(list, AdminUserEntity::getDeptId));
        ExcelUtils.write(response, "用户数据.xls", "数据", UserResponse.class,
                UserConverter.INSTANCE.convertList(list, deptMap));
    }

    @GetMapping("/get-import-template")
    @Operation(summary = "get import user template")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 手动创建导出 demo
        List<UserImportExcelDto> list = Arrays.asList(
                UserImportExcelDto.builder().username("yunai").deptId(1L).email("admin@example.com").mobile("15601691300")
                        .nickname("芋道").status(CommonStatusEnum.ENABLE.getStatus()).sex(SexEnum.MALE.getSex()).build(),
                UserImportExcelDto.builder().username("yuanma").deptId(2L).email("ops@example.com").mobile("15601701300")
                        .nickname("源码").status(CommonStatusEnum.DISABLE.getStatus()).sex(SexEnum.FEMALE.getSex()).build()
        );
        // 输出
        ExcelUtils.write(response, "用户导入模板.xls", "用户列表", UserImportExcelDto.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "import user")
    @Parameters({
            @Parameter(name = "file", description = "Excel file", required = true),
            @Parameter(name = "updateSupport", description = "support update, default as false", example = "true")
    })
    @PreAuthorize("@ss.hasPermission('system:user:import')")
    public CommonResult<UserImportResponse> importExcel(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "updateSupport", required = false, defaultValue = "false") Boolean updateSupport) throws Exception {
        List<UserImportExcelDto> list = ExcelUtils.read(file, UserImportExcelDto.class);
        return success(userService.importUserList(list, updateSupport));
    }

}

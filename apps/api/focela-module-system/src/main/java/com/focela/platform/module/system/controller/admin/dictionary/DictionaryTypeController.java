package com.focela.platform.module.system.controller.admin.dictionary;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypeResponse;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypeSimpleResponse;
import com.focela.platform.module.system.repository.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.module.system.service.dictionary.DictionaryTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "管理后台 - 字典类型")
@RestController
@RequestMapping("/system/dict-type")
@Validated
public class DictionaryTypeController {

    @Resource
    private DictionaryTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictType(@Valid @RequestBody DictionaryTypeSaveRequest createRequest) {
        Long dictTypeId = dictTypeService.createDictType(createRequest);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictType(@Valid @RequestBody DictionaryTypeSaveRequest updateRequest) {
        dictTypeService.updateDictType(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除字典类型")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictTypeList(@RequestParam("ids") List<Long> ids) {
        dictTypeService.deleteDictTypeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页列表")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictionaryTypeResponse>> pageDictTypes(@Valid DictionaryTypePageRequest pageRequest) {
        PageResult<DictionaryTypeEntity> pageResult = dictTypeService.getDictTypePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, DictionaryTypeResponse.class));
    }

    @Operation(summary = "/查询字典类型详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictionaryTypeResponse> getDictType(@RequestParam("id") Long id) {
        DictionaryTypeEntity dictType = dictTypeService.getDictType(id);
        return success(BeanUtils.toBean(dictType, DictionaryTypeResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictionaryTypeSimpleResponse>> getSimpleDictTypeList() {
        List<DictionaryTypeEntity> list = dictTypeService.getDictTypeList();
        return success(BeanUtils.toBean(list, DictionaryTypeSimpleResponse.class));
    }

    @Operation(summary = "导出数据类型")
    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictionaryTypePageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictionaryTypeEntity> list = dictTypeService.getDictTypePage(exportRequest).getList();
        // 导出
        ExcelUtils.write(response, "字典类型.xls", "数据", DictionaryTypeResponse.class,
                BeanUtils.toBean(list, DictionaryTypeResponse.class));
    }

}

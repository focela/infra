package com.focela.platform.system.controller.admin.dictionary;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypeResponse;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypeSimpleResponse;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.service.dictionary.DictionaryTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Dictionary type")
@RestController
@RequestMapping("/system/dict-type")
@Validated
@RequiredArgsConstructor
public class DictionaryTypeController {

    private final DictionaryTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "create dictionary type")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictType(@Valid @RequestBody DictionaryTypeSaveRequest createRequest) {
        Long dictTypeId = dictTypeService.createDictType(createRequest);
        return success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "update dictionary type")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictType(@Valid @RequestBody DictionaryTypeSaveRequest updateRequest) {
        dictTypeService.updateDictType(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete dictionary type")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete dictionary type")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictTypeList(@RequestParam("ids") List<Long> ids) {
        dictTypeService.deleteDictTypeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "get dictionary type page list")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictionaryTypeResponse>> pageDictTypes(@Valid DictionaryTypePageRequest pageRequest) {
        PageResult<DictionaryTypeEntity> pageResult = dictTypeService.getDictTypePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, DictionaryTypeResponse.class));
    }

    @Operation(summary = "/query dictionary type details")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictionaryTypeResponse> getDictType(@RequestParam("id") Long id) {
        DictionaryTypeEntity dictType = dictTypeService.getDictType(id);
        return success(BeanUtils.toBean(dictType, DictionaryTypeResponse.class));
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "get all dictionary type list", description = "include enable + disable dictionary type, for frontend dropdown options")
    // no permission check required as the frontend uses this globally
    public CommonResult<List<DictionaryTypeSimpleResponse>> getSimpleDictTypeList() {
        List<DictionaryTypeEntity> list = dictTypeService.getDictTypeList();
        return success(BeanUtils.toBean(list, DictionaryTypeSimpleResponse.class));
    }

    @Operation(summary = "export data type")
    @GetMapping("/export-excel")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictionaryTypePageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictionaryTypeEntity> list = dictTypeService.getDictTypePage(exportRequest).getList();
        // export
        ExcelUtils.write(response, "Dictionary Type.xls", "Data", DictionaryTypeResponse.class,
                BeanUtils.toBean(list, DictionaryTypeResponse.class));
    }

}

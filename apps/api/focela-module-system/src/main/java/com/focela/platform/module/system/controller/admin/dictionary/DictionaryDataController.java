package com.focela.platform.module.system.controller.admin.dictionary;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.module.system.controller.admin.dictionary.dto.data.DictionaryDataPageRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.data.DictionaryDataResponse;
import com.focela.platform.module.system.controller.admin.dictionary.dto.data.DictionaryDataSaveRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.data.DictionaryDataSimpleResponse;
import com.focela.platform.module.system.repository.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.module.system.service.dictionary.DictionaryDataService;
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

@Tag(name = "管理后台 - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class DictionaryDataController {

    @Resource
    private DictionaryDataService dictDataService;

    @PostMapping("/create")
    @Operation(summary = "新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictData(@Valid @RequestBody DictionaryDataSaveRequest createRequest) {
        Long dictDataId = dictDataService.createDictData(createRequest);
        return success(dictDataId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictData(@Valid @RequestBody DictionaryDataSaveRequest updateRequest) {
        dictDataService.updateDictData(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictData(@RequestParam("id") Long id) {
        dictDataService.deleteDictData(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除字典数据")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictDataList(@RequestParam("ids") List<Long> ids) {
        dictDataService.deleteDictDataList(ids);
        return success(true);
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获得全部字典数据列表", description = "一般用于管理后台缓存字典数据在本地")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictionaryDataSimpleResponse>> getSimpleDictDataList() {
        List<DictionaryDataEntity> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), null);
        return success(BeanUtils.toBean(list, DictionaryDataSimpleResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictionaryDataResponse>> getDictTypePage(@Valid DictionaryDataPageRequest pageRequest) {
        PageResult<DictionaryDataEntity> pageResult = dictDataService.getDictDataPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, DictionaryDataResponse.class));
    }

    @GetMapping(value = "/get")
    @Operation(summary = "/查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictionaryDataResponse> getDictData(@RequestParam("id") Long id) {
        DictionaryDataEntity dictData = dictDataService.getDictData(id);
        return success(BeanUtils.toBean(dictData, DictionaryDataResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictionaryDataPageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictionaryDataEntity> list = dictDataService.getDictDataPage(exportRequest).getList();
        // 输出
        ExcelUtils.write(response, "字典数据.xls", "数据", DictionaryDataResponse.class,
                BeanUtils.toBean(list, DictionaryDataResponse.class));
    }

}

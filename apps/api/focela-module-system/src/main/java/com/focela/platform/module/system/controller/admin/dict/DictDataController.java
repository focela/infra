package com.focela.platform.module.system.controller.admin.dict;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.pojo.PageParam;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.framework.excel.core.util.ExcelUtils;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataPageRequest;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataResponse;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataSaveRequest;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataSimpleResponse;
import com.focela.platform.module.system.repository.entity.dict.DictDataEntity;
import com.focela.platform.module.system.service.dict.DictDataService;
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
import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 字典数据")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class DictDataController {

    @Resource
    private DictDataService dictDataService;

    @PostMapping("/create")
    @Operation(summary = "新增字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictData(@Valid @RequestBody DictDataSaveRequest createRequest) {
        Long dictDataId = dictDataService.createDictData(createRequest);
        return success(dictDataId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictData(@Valid @RequestBody DictDataSaveRequest updateRequest) {
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
    public CommonResult<List<DictDataSimpleResponse>> getSimpleDictDataList() {
        List<DictDataEntity> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), null);
        return success(BeanUtils.toBean(list, DictDataSimpleResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得字典类型的分页")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictDataResponse>> getDictTypePage(@Valid DictDataPageRequest pageRequest) {
        PageResult<DictDataEntity> pageResult = dictDataService.getDictDataPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, DictDataResponse.class));
    }

    @GetMapping(value = "/get")
    @Operation(summary = "/查询字典数据详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictDataResponse> getDictData(@RequestParam("id") Long id) {
        DictDataEntity dictData = dictDataService.getDictData(id);
        return success(BeanUtils.toBean(dictData, DictDataResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出字典数据")
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictDataPageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictDataEntity> list = dictDataService.getDictDataPage(exportRequest).getList();
        // 输出
        ExcelUtils.write(response, "字典数据.xls", "数据", DictDataResponse.class,
                BeanUtils.toBean(list, DictDataResponse.class));
    }

}

package com.focela.platform.system.controller.admin.dictionary;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.response.data.DictionaryDataResponse;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataSaveRequest;
import com.focela.platform.system.controller.admin.dictionary.response.data.DictionaryDataSimpleResponse;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.service.dictionary.DictionaryDataService;
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

@Tag(name = "Admin - Dictionary data")
@RestController
@RequestMapping("/system/dict-data")
@Validated
@RequiredArgsConstructor
public class DictionaryDataController {

    private final DictionaryDataService dictionaryDataService;

    @PostMapping("/create")
    @Operation(summary = "create dictionary data")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictData(@Valid @RequestBody DictionaryDataSaveRequest createRequest) {
        Long dictionaryDataId = dictionaryDataService.createDictionaryData(createRequest);
        return success(dictionaryDataId);
    }

    @PutMapping("/update")
    @Operation(summary = "update dictionary data")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictData(@Valid @RequestBody DictionaryDataSaveRequest updateRequest) {
        dictionaryDataService.updateDictionaryData(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete dictionary data")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictData(@RequestParam("id") Long id) {
        dictionaryDataService.deleteDictionaryData(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete dictionary data")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictDataList(@RequestParam("ids") List<Long> ids) {
        dictionaryDataService.deleteDictionaryDataList(ids);
        return success(true);
    }

    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "get all dictionary data list", description = "usually for Admin cache dictionary data in local")
    // no permission check required as the frontend uses this globally
    public CommonResult<List<DictionaryDataSimpleResponse>> getSimpleDictDataList() {
        List<DictionaryDataEntity> dictionaryData = dictionaryDataService.getDictionaryDataList(
                CommonStatusEnum.ENABLE.getStatus(), null);
        return success(BeanUtils.toBean(dictionaryData, DictionaryDataSimpleResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get dictionary type page")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictionaryDataResponse>> getDictTypePage(@Valid DictionaryDataPageRequest pageRequest) {
        PageResult<DictionaryDataEntity> pageResult = dictionaryDataService.getDictionaryDataPage(pageRequest);
        return success(BeanUtils.toBean(pageResult, DictionaryDataResponse.class));
    }

    @GetMapping(value = "/get")
    @Operation(summary = "/query dictionary data details")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictionaryDataResponse> getDictData(@RequestParam("id") Long id) {
        DictionaryDataEntity dictionaryData = dictionaryDataService.getDictionaryData(id);
        return success(BeanUtils.toBean(dictionaryData, DictionaryDataResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export dictionary data")
    @PreAuthorize("@ss.hasPermission('system:dict:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Valid DictionaryDataPageRequest exportRequest) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DictionaryDataEntity> dictionaryData = dictionaryDataService.getDictionaryDataPage(exportRequest).getList();
        // output
        ExcelUtils.write(response, "Dictionary Data.xls", "Data", DictionaryDataResponse.class,
                BeanUtils.toBean(dictionaryData, DictionaryDataResponse.class));
    }

}

package com.focela.platform.system.controller.app.dictionary;

import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.app.dictionary.dto.AppDictionaryDataResponse;
import com.focela.platform.system.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.service.dictionary.DictionaryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "User App - Dictionary data")
@RestController
@RequestMapping("/system/dict-data")
@Validated
public class AppDictionaryDataController {

    @Resource
    private DictionaryDataService dictDataService;

    @GetMapping("/type")
    @Operation(summary = "by dictionary type query dictionary data info")
    @Parameter(name = "type", description = "Dictionary type", required = true, example = "common_status")
    @PermitAll
    public CommonResult<List<AppDictionaryDataResponse>> getDictDataListByType(@RequestParam("type") String type) {
        List<DictionaryDataEntity> list = dictDataService.getDictDataList(
                CommonStatusEnum.ENABLE.getStatus(), type);
        return success(BeanUtils.toBean(list, AppDictionaryDataResponse.class));
    }

}

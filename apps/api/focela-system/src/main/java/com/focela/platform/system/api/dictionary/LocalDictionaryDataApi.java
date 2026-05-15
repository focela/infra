package com.focela.platform.system.api.dictionary;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.api.system.dictionary.dto.DictionaryDataRpcResponse;
import com.focela.platform.system.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.service.dictionary.DictionaryDataService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Dictionary data API implementation class
 */
@Service
public class LocalDictionaryDataApi implements DictionaryDataApi {

    @Resource
    private DictionaryDataService dictDataService;

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
    }

    @Override
    public List<DictionaryDataRpcResponse> getDictDataList(String dictType) {
        List<DictionaryDataEntity> list = dictDataService.getDictDataListByDictType(dictType);
        return BeanUtils.toBean(list, DictionaryDataRpcResponse.class);
    }

}

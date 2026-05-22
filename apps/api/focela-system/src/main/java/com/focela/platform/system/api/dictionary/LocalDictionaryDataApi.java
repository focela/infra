package com.focela.platform.system.api.dictionary;

import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.common.api.system.dictionary.dto.DictionaryDataRpcResponse;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.service.dictionary.DictionaryDataService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Dictionary data API implementation class
 */
@Service
@RequiredArgsConstructor
public class LocalDictionaryDataApi implements DictionaryDataApi {

    private final DictionaryDataService dictionaryDataService;

    @Override
    public void validateDictionaryDataList(String dictionaryType, Collection<String> values) {
        dictionaryDataService.validateDictionaryDataList(dictionaryType, values);
    }

    @Override
    @Deprecated
    public void validateDictDataList(String dictionaryType, Collection<String> values) {
        validateDictionaryDataList(dictionaryType, values);
    }

    @Override
    public List<DictionaryDataRpcResponse> getDictionaryDataList(String dictionaryType) {
        List<DictionaryDataEntity> list = dictionaryDataService.getDictionaryDataListByDictionaryType(dictionaryType);
        return BeanUtils.toBean(list, DictionaryDataRpcResponse.class);
    }

    @Override
    @Deprecated
    public List<DictionaryDataRpcResponse> getDictDataList(String dictionaryType) {
        return getDictionaryDataList(dictionaryType);
    }

}

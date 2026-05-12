package com.focela.platform.module.system.api.dictionary;

import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.common.business.system.dictionary.dto.DictionaryDataRespDTO;
import com.focela.platform.module.system.repository.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.module.system.service.dictionary.DictionaryDataService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class DictionaryDataApiImpl implements DictionaryDataApi {

    @Resource
    private DictionaryDataService dictDataService;

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
    }

    @Override
    public List<DictionaryDataRespDTO> getDictDataList(String dictType) {
        List<DictionaryDataEntity> list = dictDataService.getDictDataListByDictType(dictType);
        return BeanUtils.toBean(list, DictionaryDataRespDTO.class);
    }

}

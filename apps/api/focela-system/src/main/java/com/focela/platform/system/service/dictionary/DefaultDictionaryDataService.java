package com.focela.platform.system.service.dictionary;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.dictionary.dto.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.data.DictionaryDataSaveRequest;
import com.focela.platform.system.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryDataMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * Dictionary data Service implementation class
 */
@Service
@Slf4j
public class DefaultDictionaryDataService implements DictionaryDataService {

    /**
     * Sort by dictType, then by sort
     */
    private static final Comparator<DictionaryDataEntity> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictionaryDataEntity::getDictType)
            .thenComparingInt(DictionaryDataEntity::getSort);

    @Resource
    private DictionaryTypeService dictTypeService;

    @Resource
    private DictionaryDataMapper dictDataMapper;

    @Override
    public List<DictionaryDataEntity> getDictDataList(Integer status, String dictType) {
        List<DictionaryDataEntity> list = dictDataMapper.selectListByStatusAndDictType(status, dictType);
        list.sort(COMPARATOR_TYPE_AND_SORT);
        return list;
    }

    @Override
    public PageResult<DictionaryDataEntity> getDictDataPage(DictionaryDataPageRequest pageRequest) {
        return dictDataMapper.selectPage(pageRequest);
    }

    @Override
    public DictionaryDataEntity getDictData(Long id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    public Long createDictData(DictionaryDataSaveRequest createRequest) {
        // Validate that the dictionary type is valid
        validateDictTypeExists(createRequest.getDictType());
        // Validate uniqueness of the dictionary data value
        validateDictDataValueUnique(null, createRequest.getDictType(), createRequest.getValue());

        // Insert dictionary data
        DictionaryDataEntity dictData = BeanUtils.toBean(createRequest, DictionaryDataEntity.class);
        dictDataMapper.insert(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictionaryDataSaveRequest updateRequest) {
        // Validate that the dictionary data exists
        validateDictDataExists(updateRequest.getId());
        // Validate that the dictionary type is valid
        validateDictTypeExists(updateRequest.getDictType());
        // Validate uniqueness of the dictionary data value
        validateDictDataValueUnique(updateRequest.getId(), updateRequest.getDictType(), updateRequest.getValue());

        // Update dictionary data
        DictionaryDataEntity updateObj = BeanUtils.toBean(updateRequest, DictionaryDataEntity.class);
        dictDataMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // Validate existence
        validateDictDataExists(id);

        // Delete dictionary data
        dictDataMapper.deleteById(id);
    }

    @Override
    public void deleteDictDataList(List<Long> ids) {
        dictDataMapper.deleteByIds(ids);
    }

    @Override
    public long getDictDataCountByDictType(String dictType) {
        return dictDataMapper.selectCountByDictType(dictType);
    }

    @VisibleForTesting
    public void validateDictDataValueUnique(Long id, String dictType, String value) {
        DictionaryDataEntity dictData = dictDataMapper.selectByDictTypeAndValue(dictType, value);
        if (dictData == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary data id
        if (id == null) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
        if (!dictData.getId().equals(id)) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictDataExists(Long id) {
        if (id == null) {
            return;
        }
        DictionaryDataEntity dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    public void validateDictTypeExists(String type) {
        DictionaryTypeEntity dictType = dictTypeService.getDictType(type);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictType.getStatus())) {
            throw exception(DICT_TYPE_NOT_ENABLE);
        }
    }

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        Map<String, DictionaryDataEntity> dictDataMap = CollectionUtils.convertMap(
                dictDataMapper.selectByDictTypeAndValues(dictType, values), DictionaryDataEntity::getValue);
        // Validate
        values.forEach(value -> {
            DictionaryDataEntity dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw exception(DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw exception(DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @Override
    public DictionaryDataEntity getDictData(String dictType, String value) {
        return dictDataMapper.selectByDictTypeAndValue(dictType, value);
    }

    @Override
    public DictionaryDataEntity parseDictData(String dictType, String label) {
        return dictDataMapper.selectByDictTypeAndLabel(dictType, label);
    }

    @Override
    public List<DictionaryDataEntity> getDictDataListByDictType(String dictType) {
        List<DictionaryDataEntity> list = dictDataMapper.selectList(DictionaryDataEntity::getDictType, dictType);
        list.sort(Comparator.comparing(DictionaryDataEntity::getSort));
        return list;
    }

}

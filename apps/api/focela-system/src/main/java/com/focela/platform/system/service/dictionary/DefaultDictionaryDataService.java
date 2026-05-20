package com.focela.platform.system.service.dictionary;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.common.enums.CommonStatusEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.collection.CollectionUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataPageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import com.focela.platform.system.domain.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryDataMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Dictionary data Service implementation class
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultDictionaryDataService implements DictionaryDataService {

    /**
     * Sort by dictionaryType, then by sort
     */
    private static final Comparator<DictionaryDataEntity> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictionaryDataEntity::getDictType)
            .thenComparingInt(DictionaryDataEntity::getSort);

    private final DictionaryDataMapper dictionaryDataMapper;

    /**
     * Lazy field injection breaks the {@code DefaultDictionaryDataService} ↔
     * {@code DefaultDictionaryTypeService} cycle.
     * See MODULE_TEMPLATE.md §12.5.
     */
    @Resource
    @Lazy
    private DictionaryTypeService dictionaryTypeService;

    @Override
    public List<DictionaryDataEntity> getDictDataList(Integer status, String dictionaryType) {
        List<DictionaryDataEntity> dictionaryData = dictionaryDataMapper.selectListByStatusAndDictType(status, dictionaryType);
        dictionaryData.sort(COMPARATOR_TYPE_AND_SORT);
        return dictionaryData;
    }

    @Override
    public PageResult<DictionaryDataEntity> getDictDataPage(DictionaryDataPageRequest pageRequest) {
        return dictionaryDataMapper.selectPage(pageRequest);
    }

    @Override
    public DictionaryDataEntity getDictData(Long id) {
        return dictionaryDataMapper.selectById(id);
    }

    @Override
    public Long createDictData(DictionaryDataSaveRequest createRequest) {
        // Validate that the dictionary type is valid
        validateDictTypeExists(createRequest.getDictType());
        // Validate uniqueness of the dictionary data value
        validateDictDataValueUnique(null, createRequest.getDictType(), createRequest.getValue());

        // Insert dictionary data
        DictionaryDataEntity dictionaryData = BeanUtils.toBean(createRequest, DictionaryDataEntity.class);
        dictionaryDataMapper.insert(dictionaryData);
        return dictionaryData.getId();
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
        dictionaryDataMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // Validate existence
        validateDictDataExists(id);

        // Delete dictionary data
        dictionaryDataMapper.deleteById(id);
    }

    @Override
    public void deleteDictDataList(List<Long> ids) {
        dictionaryDataMapper.deleteByIds(ids);
    }

    @Override
    public long getDictDataCountByDictType(String dictionaryType) {
        return dictionaryDataMapper.selectCountByDictType(dictionaryType);
    }

    @VisibleForTesting
    public void validateDictDataValueUnique(Long id, String dictionaryType, String value) {
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectByDictTypeAndValue(dictionaryType, value);
        if (dictionaryData == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary data id
        if (id == null) {
            throw exception(DICTIONARY_DATA_VALUE_DUPLICATE);
        }
        if (!dictionaryData.getId().equals(id)) {
            throw exception(DICTIONARY_DATA_VALUE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictDataExists(Long id) {
        if (id == null) {
            return;
        }
        DictionaryDataEntity dictionaryData = dictionaryDataMapper.selectById(id);
        if (dictionaryData == null) {
            throw exception(DICTIONARY_DATA_NOT_FOUND);
        }
    }

    @VisibleForTesting
    public void validateDictTypeExists(String type) {
        DictionaryTypeEntity dictionaryType = dictionaryTypeService.getDictType(type);
        if (dictionaryType == null) {
            throw exception(DICTIONARY_TYPE_NOT_FOUND);
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictionaryType.getStatus())) {
            throw exception(DICTIONARY_TYPE_NOT_ENABLED);
        }
    }

    @Override
    public void validateDictDataList(String dictionaryType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        Map<String, DictionaryDataEntity> dictionaryDataMap = CollectionUtils.convertMap(
                dictionaryDataMapper.selectByDictTypeAndValues(dictionaryType, values), DictionaryDataEntity::getValue);
        // Validate
        values.forEach(value -> {
            DictionaryDataEntity dictionaryData = dictionaryDataMap.get(value);
            if (dictionaryData == null) {
                throw exception(DICTIONARY_DATA_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictionaryData.getStatus())) {
                throw exception(DICTIONARY_DATA_NOT_ENABLED, dictionaryData.getLabel());
            }
        });
    }

    @Override
    public DictionaryDataEntity getDictData(String dictionaryType, String value) {
        return dictionaryDataMapper.selectByDictTypeAndValue(dictionaryType, value);
    }

    @Override
    public DictionaryDataEntity parseDictData(String dictionaryType, String label) {
        return dictionaryDataMapper.selectByDictTypeAndLabel(dictionaryType, label);
    }

    @Override
    public List<DictionaryDataEntity> getDictDataListByDictType(String dictionaryType) {
        List<DictionaryDataEntity> dictionaryData = dictionaryDataMapper.selectList(DictionaryDataEntity::getDictType, dictionaryType);
        dictionaryData.sort(Comparator.comparing(DictionaryDataEntity::getSort));
        return dictionaryData;
    }

}

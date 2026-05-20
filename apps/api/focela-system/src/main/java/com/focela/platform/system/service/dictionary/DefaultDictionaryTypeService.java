package com.focela.platform.system.service.dictionary;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.date.LocalDateTimeUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.request.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.SystemErrorCodeConstants.*;

/**
 * Dictionary type Service implementation class
 */
@Service
@RequiredArgsConstructor
public class DefaultDictionaryTypeService implements DictionaryTypeService {

    private final DictionaryDataService dictionaryDataService;

    private final DictionaryTypeMapper dictionaryTypeMapper;

    @Override
    public PageResult<DictionaryTypeEntity> getDictTypePage(DictionaryTypePageRequest pageRequest) {
        return dictionaryTypeMapper.selectPage(pageRequest);
    }

    @Override
    public DictionaryTypeEntity getDictType(Long id) {
        return dictionaryTypeMapper.selectById(id);
    }

    @Override
    public DictionaryTypeEntity getDictType(String type) {
        return dictionaryTypeMapper.selectByType(type);
    }

    @Override
    public Long createDictType(DictionaryTypeSaveRequest createRequest) {
        // Validate uniqueness of the dictionary type name
        validateDictTypeNameUnique(null, createRequest.getName());
        // Validate uniqueness of the dictionary type code
        validateDictTypeUnique(null, createRequest.getType());

        // Insert dictionary type
        DictionaryTypeEntity dictionaryType = BeanUtils.toBean(createRequest, DictionaryTypeEntity.class);
        dictionaryType.setDeletedTime(LocalDateTimeUtils.EMPTY); // unique index, avoid null value
        dictionaryTypeMapper.insert(dictionaryType);
        return dictionaryType.getId();
    }

    @Override
    public void updateDictType(DictionaryTypeSaveRequest updateRequest) {
        // Validate that the dictionary type exists
        validateDictTypeExists(updateRequest.getId());
        // Validate uniqueness of the dictionary type name
        validateDictTypeNameUnique(updateRequest.getId(), updateRequest.getName());
        // Validate uniqueness of the dictionary type code
        validateDictTypeUnique(updateRequest.getId(), updateRequest.getType());

        // Update dictionary type
        DictionaryTypeEntity updateObj = BeanUtils.toBean(updateRequest, DictionaryTypeEntity.class);
        dictionaryTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // Validate existence
        DictionaryTypeEntity dictionaryType = validateDictTypeExists(id);
        // Validate whether dictionary data exists
        if (dictionaryDataService.getDictDataCountByDictType(dictionaryType.getType()) > 0) {
            throw exception(DICTIONARY_TYPE_HAS_CHILDREN);
        }
        // Delete dictionary type
        dictionaryTypeMapper.updateToDelete(id, LocalDateTime.now());
    }

    @Override
    public void deleteDictTypeList(List<Long> ids) {
        // 1. Validate whether dictionary data exists
        List<DictionaryTypeEntity> dictionaryTypes = dictionaryTypeMapper.selectByIds(ids);
        dictionaryTypes.forEach(dictionaryType -> {
            if (dictionaryDataService.getDictDataCountByDictType(dictionaryType.getType()) > 0) {
                throw exception(DICTIONARY_TYPE_HAS_CHILDREN);
            }
        });

        // 2. Batch delete dictionary types
        LocalDateTime now = LocalDateTime.now();
        ids.forEach(id -> dictionaryTypeMapper.updateToDelete(id, now));
    }

    @Override
    public List<DictionaryTypeEntity> getDictTypeList() {
        return dictionaryTypeMapper.selectList();
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectByName(name);
        if (dictionaryType == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary type id
        if (id == null) {
            throw exception(DICTIONARY_TYPE_NAME_DUPLICATE);
        }
        if (!dictionaryType.getId().equals(id)) {
            throw exception(DICTIONARY_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectByType(type);
        if (dictionaryType == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary type id
        if (id == null) {
            throw exception(DICTIONARY_TYPE_TYPE_DUPLICATE);
        }
        if (!dictionaryType.getId().equals(id)) {
            throw exception(DICTIONARY_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    DictionaryTypeEntity validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictionaryTypeEntity dictionaryType = dictionaryTypeMapper.selectById(id);
        if (dictionaryType == null) {
            throw exception(DICTIONARY_TYPE_NOT_FOUND);
        }
        return dictionaryType;
    }

}

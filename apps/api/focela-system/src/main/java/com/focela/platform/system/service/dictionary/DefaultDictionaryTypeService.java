package com.focela.platform.system.service.dictionary;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.date.LocalDateTimeUtils;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.system.repository.mapper.dictionary.DictionaryTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.system.constants.ErrorCodeConstants.*;

/**
 * Dictionary type Service implementation class
 */
@Service
public class DefaultDictionaryTypeService implements DictionaryTypeService {

    @Resource
    private DictionaryDataService dictDataService;

    @Resource
    private DictionaryTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictionaryTypeEntity> getDictTypePage(DictionaryTypePageRequest pageRequest) {
        return dictTypeMapper.selectPage(pageRequest);
    }

    @Override
    public DictionaryTypeEntity getDictType(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public DictionaryTypeEntity getDictType(String type) {
        return dictTypeMapper.selectByType(type);
    }

    @Override
    public Long createDictType(DictionaryTypeSaveRequest createRequest) {
        // Validate uniqueness of the dictionary type name
        validateDictTypeNameUnique(null, createRequest.getName());
        // Validate uniqueness of the dictionary type code
        validateDictTypeUnique(null, createRequest.getType());

        // Insert dictionary type
        DictionaryTypeEntity dictType = BeanUtils.toBean(createRequest, DictionaryTypeEntity.class);
        dictType.setDeletedTime(LocalDateTimeUtils.EMPTY); // unique index, avoid null value
        dictTypeMapper.insert(dictType);
        return dictType.getId();
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
        dictTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // Validate existence
        DictionaryTypeEntity dictType = validateDictTypeExists(id);
        // Validate whether dictionary data exists
        if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // Delete dictionary type
        dictTypeMapper.updateToDelete(id, LocalDateTime.now());
    }

    @Override
    public void deleteDictTypeList(List<Long> ids) {
        // 1. Validate whether dictionary data exists
        List<DictionaryTypeEntity> dictTypes = dictTypeMapper.selectByIds(ids);
        dictTypes.forEach(dictType -> {
            if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
                throw exception(DICT_TYPE_HAS_CHILDREN);
            }
        });

        // 2. Batch delete dictionary types
        LocalDateTime now = LocalDateTime.now();
        ids.forEach(id -> dictTypeMapper.updateToDelete(id, now));
    }

    @Override
    public List<DictionaryTypeEntity> getDictTypeList() {
        return dictTypeMapper.selectList();
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        DictionaryTypeEntity dictType = dictTypeMapper.selectByName(name);
        if (dictType == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary type id
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictionaryTypeEntity dictType = dictTypeMapper.selectByType(type);
        if (dictType == null) {
            return;
        }
        // If id is null, no need to compare whether it is the same dictionary type id
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    DictionaryTypeEntity validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictionaryTypeEntity dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

}

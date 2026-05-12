package com.focela.platform.module.system.service.dictionary;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.date.LocalDateTimeUtils;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.module.system.controller.admin.dictionary.dto.type.DictionaryTypeSaveRequest;
import com.focela.platform.module.system.repository.entity.dictionary.DictionaryTypeEntity;
import com.focela.platform.module.system.repository.mapper.dictionary.DictionaryTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.framework.common.exception.utils.ServiceExceptionUtils.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典类型 Service 实现类
 *
 * @author 芋道源码
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
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(null, createRequest.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(null, createRequest.getType());

        // 插入字典类型
        DictionaryTypeEntity dictType = BeanUtils.toBean(createRequest, DictionaryTypeEntity.class);
        dictType.setDeletedTime(LocalDateTimeUtils.EMPTY); // 唯一索引，避免 null 值
        dictTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictionaryTypeSaveRequest updateRequest) {
        // 校验自己存在
        validateDictTypeExists(updateRequest.getId());
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(updateRequest.getId(), updateRequest.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(updateRequest.getId(), updateRequest.getType());

        // 更新字典类型
        DictionaryTypeEntity updateObj = BeanUtils.toBean(updateRequest, DictionaryTypeEntity.class);
        dictTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictionaryTypeEntity dictType = validateDictTypeExists(id);
        // 校验是否有字典数据
        if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // 删除字典类型
        dictTypeMapper.updateToDelete(id, LocalDateTime.now());
    }

    @Override
    public void deleteDictTypeList(List<Long> ids) {
        // 1. 校验是否有字典数据
        List<DictionaryTypeEntity> dictTypes = dictTypeMapper.selectByIds(ids);
        dictTypes.forEach(dictType -> {
            if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
                throw exception(DICT_TYPE_HAS_CHILDREN);
            }
        });

        // 2. 批量删除字典类型
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
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
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
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
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

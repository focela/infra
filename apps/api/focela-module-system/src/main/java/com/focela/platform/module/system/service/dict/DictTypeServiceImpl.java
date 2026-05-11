package com.focela.platform.module.system.service.dict;

import cn.hutool.core.util.StrUtil;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.date.LocalDateTimeUtils;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.focela.platform.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.focela.platform.module.system.repository.entity.dict.DictTypeEntity;
import com.focela.platform.module.system.repository.mapper.dict.DictTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典类型 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictTypeEntity> getDictTypePage(DictTypePageReqVO pageReqVO) {
        return dictTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public DictTypeEntity getDictType(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public DictTypeEntity getDictType(String type) {
        return dictTypeMapper.selectByType(type);
    }

    @Override
    public Long createDictType(DictTypeSaveReqVO createReqVO) {
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(null, createReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(null, createReqVO.getType());

        // 插入字典类型
        DictTypeEntity dictType = BeanUtils.toBean(createReqVO, DictTypeEntity.class);
        dictType.setDeletedTime(LocalDateTimeUtils.EMPTY); // 唯一索引，避免 null 值
        dictTypeMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public void updateDictType(DictTypeSaveReqVO updateReqVO) {
        // 校验自己存在
        validateDictTypeExists(updateReqVO.getId());
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(updateReqVO.getId(), updateReqVO.getType());

        // 更新字典类型
        DictTypeEntity updateObj = BeanUtils.toBean(updateReqVO, DictTypeEntity.class);
        dictTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictTypeEntity dictType = validateDictTypeExists(id);
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
        List<DictTypeEntity> dictTypes = dictTypeMapper.selectByIds(ids);
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
    public List<DictTypeEntity> getDictTypeList() {
        return dictTypeMapper.selectList();
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        DictTypeEntity dictType = dictTypeMapper.selectByName(name);
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
        DictTypeEntity dictType = dictTypeMapper.selectByType(type);
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
    DictTypeEntity validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeEntity dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

}

package com.focela.platform.module.system.service.dict;

import cn.hutool.core.collection.CollUtil;
import com.focela.platform.framework.common.enums.CommonStatusEnum;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.collection.CollectionUtils;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataPageRequest;
import com.focela.platform.module.system.controller.admin.dict.dto.data.DictDataSaveRequest;
import com.focela.platform.module.system.repository.entity.dict.DictDataEntity;
import com.focela.platform.module.system.repository.entity.dict.DictTypeEntity;
import com.focela.platform.module.system.repository.mapper.dict.DictDataMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.focela.platform.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.focela.platform.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典数据 Service 实现类
 *
 * @author ruoyi
 */
@Service
@Slf4j
public class DictDataServiceImpl implements DictDataService {

    /**
     * 排序 dictType > sort
     */
    private static final Comparator<DictDataEntity> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictDataEntity::getDictType)
            .thenComparingInt(DictDataEntity::getSort);

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private DictDataMapper dictDataMapper;

    @Override
    public List<DictDataEntity> getDictDataList(Integer status, String dictType) {
        List<DictDataEntity> list = dictDataMapper.selectListByStatusAndDictType(status, dictType);
        list.sort(COMPARATOR_TYPE_AND_SORT);
        return list;
    }

    @Override
    public PageResult<DictDataEntity> getDictDataPage(DictDataPageRequest pageRequest) {
        return dictDataMapper.selectPage(pageRequest);
    }

    @Override
    public DictDataEntity getDictData(Long id) {
        return dictDataMapper.selectById(id);
    }

    @Override
    public Long createDictData(DictDataSaveRequest createRequest) {
        // 校验字典类型有效
        validateDictTypeExists(createRequest.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(null, createRequest.getDictType(), createRequest.getValue());

        // 插入字典类型
        DictDataEntity dictData = BeanUtils.toBean(createRequest, DictDataEntity.class);
        dictDataMapper.insert(dictData);
        return dictData.getId();
    }

    @Override
    public void updateDictData(DictDataSaveRequest updateRequest) {
        // 校验自己存在
        validateDictDataExists(updateRequest.getId());
        // 校验字典类型有效
        validateDictTypeExists(updateRequest.getDictType());
        // 校验字典数据的值的唯一性
        validateDictDataValueUnique(updateRequest.getId(), updateRequest.getDictType(), updateRequest.getValue());

        // 更新字典类型
        DictDataEntity updateObj = BeanUtils.toBean(updateRequest, DictDataEntity.class);
        dictDataMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictData(Long id) {
        // 校验是否存在
        validateDictDataExists(id);

        // 删除字典数据
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
        DictDataEntity dictData = dictDataMapper.selectByDictTypeAndValue(dictType, value);
        if (dictData == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典数据
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
        DictDataEntity dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    public void validateDictTypeExists(String type) {
        DictTypeEntity dictType = dictTypeService.getDictType(type);
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
        Map<String, DictDataEntity> dictDataMap = CollectionUtils.convertMap(
                dictDataMapper.selectByDictTypeAndValues(dictType, values), DictDataEntity::getValue);
        // 校验
        values.forEach(value -> {
            DictDataEntity dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw exception(DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw exception(DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @Override
    public DictDataEntity getDictData(String dictType, String value) {
        return dictDataMapper.selectByDictTypeAndValue(dictType, value);
    }

    @Override
    public DictDataEntity parseDictData(String dictType, String label) {
        return dictDataMapper.selectByDictTypeAndLabel(dictType, label);
    }

    @Override
    public List<DictDataEntity> getDictDataListByDictType(String dictType) {
        List<DictDataEntity> list = dictDataMapper.selectList(DictDataEntity::getDictType, dictType);
        list.sort(Comparator.comparing(DictDataEntity::getSort));
        return list;
    }

}

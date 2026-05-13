package com.focela.platform.module.system.repository.mapper.dictionary;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.dictionary.dto.data.DictionaryDataPageRequest;
import com.focela.platform.module.system.repository.entity.dictionary.DictionaryDataEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface DictionaryDataMapper extends BaseMapperX<DictionaryDataEntity> {

    default DictionaryDataEntity selectByDictTypeAndValue(String dictType, String value) {
        return selectOne(DictionaryDataEntity::getDictType, dictType, DictionaryDataEntity::getValue, value);
    }

    default DictionaryDataEntity selectByDictTypeAndLabel(String dictType, String label) {
        return selectOne(DictionaryDataEntity::getDictType, dictType, DictionaryDataEntity::getLabel, label);
    }

    default List<DictionaryDataEntity> selectByDictTypeAndValues(String dictType, Collection<String> values) {
        return selectList(new LambdaQueryWrapper<DictionaryDataEntity>().eq(DictionaryDataEntity::getDictType, dictType)
                .in(DictionaryDataEntity::getValue, values));
    }

    default long selectCountByDictType(String dictType) {
        return selectCount(DictionaryDataEntity::getDictType, dictType);
    }

    default PageResult<DictionaryDataEntity> selectPage(DictionaryDataPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<DictionaryDataEntity>()
                .likeIfPresent(DictionaryDataEntity::getLabel, request.getLabel())
                .eqIfPresent(DictionaryDataEntity::getDictType, request.getDictType())
                .eqIfPresent(DictionaryDataEntity::getStatus, request.getStatus())
                .orderByDesc(Arrays.asList(DictionaryDataEntity::getDictType, DictionaryDataEntity::getSort)));
    }

    default List<DictionaryDataEntity> selectListByStatusAndDictType(Integer status, String dictType) {
        return selectList(new LambdaQueryWrapperX<DictionaryDataEntity>()
                .eqIfPresent(DictionaryDataEntity::getStatus, status)
                .eqIfPresent(DictionaryDataEntity::getDictType, dictType));
    }

}

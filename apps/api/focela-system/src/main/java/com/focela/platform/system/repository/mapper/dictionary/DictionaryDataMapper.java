package com.focela.platform.system.repository.mapper.dictionary;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.dictionary.request.data.DictionaryDataPageRequest;
import com.focela.platform.system.domain.entity.dictionary.DictionaryDataEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface DictionaryDataMapper extends BaseMapperX<DictionaryDataEntity> {

    default DictionaryDataEntity selectByDictTypeAndValue(String dictionaryType, String value) {
        return selectOne(DictionaryDataEntity::getDictType, dictionaryType, DictionaryDataEntity::getValue, value);
    }

    default DictionaryDataEntity selectByDictTypeAndLabel(String dictionaryType, String label) {
        return selectOne(DictionaryDataEntity::getDictType, dictionaryType, DictionaryDataEntity::getLabel, label);
    }

    default List<DictionaryDataEntity> selectByDictTypeAndValues(String dictionaryType, Collection<String> values) {
        return selectList(new LambdaQueryWrapper<DictionaryDataEntity>().eq(DictionaryDataEntity::getDictType, dictionaryType)
                .in(DictionaryDataEntity::getValue, values));
    }

    default long selectCountByDictType(String dictionaryType) {
        return selectCount(DictionaryDataEntity::getDictType, dictionaryType);
    }

    default PageResult<DictionaryDataEntity> selectPage(DictionaryDataPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<DictionaryDataEntity>()
                .likeIfPresent(DictionaryDataEntity::getLabel, request.getLabel())
                .eqIfPresent(DictionaryDataEntity::getDictType, request.getDictType())
                .eqIfPresent(DictionaryDataEntity::getStatus, request.getStatus())
                .orderByDesc(Arrays.asList(DictionaryDataEntity::getDictType, DictionaryDataEntity::getSort)));
    }

    default List<DictionaryDataEntity> selectListByStatusAndDictType(Integer status, String dictionaryType) {
        return selectList(new LambdaQueryWrapperX<DictionaryDataEntity>()
                .eqIfPresent(DictionaryDataEntity::getStatus, status)
                .eqIfPresent(DictionaryDataEntity::getDictType, dictionaryType));
    }

}

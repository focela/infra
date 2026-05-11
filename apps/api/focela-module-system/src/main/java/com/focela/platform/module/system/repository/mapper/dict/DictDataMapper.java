package com.focela.platform.module.system.repository.mapper.dict;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.focela.platform.module.system.repository.entity.dict.DictDataEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface DictDataMapper extends BaseMapperX<DictDataEntity> {

    default DictDataEntity selectByDictTypeAndValue(String dictType, String value) {
        return selectOne(DictDataEntity::getDictType, dictType, DictDataEntity::getValue, value);
    }

    default DictDataEntity selectByDictTypeAndLabel(String dictType, String label) {
        return selectOne(DictDataEntity::getDictType, dictType, DictDataEntity::getLabel, label);
    }

    default List<DictDataEntity> selectByDictTypeAndValues(String dictType, Collection<String> values) {
        return selectList(new LambdaQueryWrapper<DictDataEntity>().eq(DictDataEntity::getDictType, dictType)
                .in(DictDataEntity::getValue, values));
    }

    default long selectCountByDictType(String dictType) {
        return selectCount(DictDataEntity::getDictType, dictType);
    }

    default PageResult<DictDataEntity> selectPage(DictDataPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictDataEntity>()
                .likeIfPresent(DictDataEntity::getLabel, reqVO.getLabel())
                .eqIfPresent(DictDataEntity::getDictType, reqVO.getDictType())
                .eqIfPresent(DictDataEntity::getStatus, reqVO.getStatus())
                .orderByDesc(Arrays.asList(DictDataEntity::getDictType, DictDataEntity::getSort)));
    }

    default List<DictDataEntity> selectListByStatusAndDictType(Integer status, String dictType) {
        return selectList(new LambdaQueryWrapperX<DictDataEntity>()
                .eqIfPresent(DictDataEntity::getStatus, status)
                .eqIfPresent(DictDataEntity::getDictType, dictType));
    }

}

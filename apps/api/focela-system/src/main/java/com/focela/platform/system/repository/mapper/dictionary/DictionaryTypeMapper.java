package com.focela.platform.system.repository.mapper.dictionary;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.dictionary.dto.type.DictionaryTypePageRequest;
import com.focela.platform.system.entity.dictionary.DictionaryTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface DictionaryTypeMapper extends BaseMapperX<DictionaryTypeEntity> {

    default PageResult<DictionaryTypeEntity> selectPage(DictionaryTypePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<DictionaryTypeEntity>()
                .likeIfPresent(DictionaryTypeEntity::getName, request.getName())
                .likeIfPresent(DictionaryTypeEntity::getType, request.getType())
                .eqIfPresent(DictionaryTypeEntity::getStatus, request.getStatus())
                .betweenIfPresent(DictionaryTypeEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(DictionaryTypeEntity::getId));
    }

    default DictionaryTypeEntity selectByType(String type) {
        return selectOne(DictionaryTypeEntity::getType, type);
    }

    default DictionaryTypeEntity selectByName(String name) {
        return selectOne(DictionaryTypeEntity::getName, name);
    }

    @Update("UPDATE system_dict_type SET deleted = 1, deleted_time = #{deletedTime} WHERE id = #{id}")
    void updateToDelete(@Param("id") Long id, @Param("deletedTime") LocalDateTime deletedTime);

}

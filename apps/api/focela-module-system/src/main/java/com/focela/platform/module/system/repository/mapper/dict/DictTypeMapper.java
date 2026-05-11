package com.focela.platform.module.system.repository.mapper.dict;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.focela.platform.module.system.repository.entity.dict.DictTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface DictTypeMapper extends BaseMapperX<DictTypeEntity> {

    default PageResult<DictTypeEntity> selectPage(DictTypePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DictTypeEntity>()
                .likeIfPresent(DictTypeEntity::getName, reqVO.getName())
                .likeIfPresent(DictTypeEntity::getType, reqVO.getType())
                .eqIfPresent(DictTypeEntity::getStatus, reqVO.getStatus())
                .betweenIfPresent(DictTypeEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DictTypeEntity::getId));
    }

    default DictTypeEntity selectByType(String type) {
        return selectOne(DictTypeEntity::getType, type);
    }

    default DictTypeEntity selectByName(String name) {
        return selectOne(DictTypeEntity::getName, name);
    }

    @Update("UPDATE system_dict_type SET deleted = 1, deleted_time = #{deletedTime} WHERE id = #{id}")
    void updateToDelete(@Param("id") Long id, @Param("deletedTime") LocalDateTime deletedTime);

}

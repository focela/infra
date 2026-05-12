package com.focela.platform.module.infra.repository.mapper.demo.demo03.normal;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.demo.demo03.normal.dto.Demo03StudentNormalPageRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03StudentNormalMapper extends BaseMapperX<Demo03StudentEntity> {

    default PageResult<Demo03StudentEntity> selectPage(Demo03StudentNormalPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<Demo03StudentEntity>()
                .likeIfPresent(Demo03StudentEntity::getName, request.getName())
                .eqIfPresent(Demo03StudentEntity::getSex, request.getSex())
                .eqIfPresent(Demo03StudentEntity::getDescription, request.getDescription())
                .betweenIfPresent(Demo03StudentEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(Demo03StudentEntity::getId));
    }

}
package com.focela.platform.module.infra.repository.mapper.demo.demo03.inner;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.demo.demo03.inner.dto.Demo03StudentInnerPageRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo03.Demo03StudentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo03StudentInnerMapper extends BaseMapperX<Demo03StudentEntity> {

    default PageResult<Demo03StudentEntity> selectPage(Demo03StudentInnerPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo03StudentEntity>()
                .likeIfPresent(Demo03StudentEntity::getName, reqVO.getName())
                .eqIfPresent(Demo03StudentEntity::getSex, reqVO.getSex())
                .eqIfPresent(Demo03StudentEntity::getDescription, reqVO.getDescription())
                .betweenIfPresent(Demo03StudentEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo03StudentEntity::getId));
    }

}
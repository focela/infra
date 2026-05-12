package com.focela.platform.module.infra.repository.mapper.demo.demo01;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.demo.demo01.dto.Demo01ContactPageRequest;
import com.focela.platform.module.infra.repository.entity.demo.demo01.Demo01ContactEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例联系人 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface Demo01ContactMapper extends BaseMapperX<Demo01ContactEntity> {

    default PageResult<Demo01ContactEntity> selectPage(Demo01ContactPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<Demo01ContactEntity>()
                .likeIfPresent(Demo01ContactEntity::getName, request.getName())
                .eqIfPresent(Demo01ContactEntity::getSex, request.getSex())
                .betweenIfPresent(Demo01ContactEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(Demo01ContactEntity::getId));
    }

}
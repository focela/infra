package com.focela.platform.module.infra.repository.mapper.demo.demo01;

import com.focela.platform.framework.common.pojo.PageResult;
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

    default PageResult<Demo01ContactEntity> selectPage(Demo01ContactPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<Demo01ContactEntity>()
                .likeIfPresent(Demo01ContactEntity::getName, reqVO.getName())
                .eqIfPresent(Demo01ContactEntity::getSex, reqVO.getSex())
                .betweenIfPresent(Demo01ContactEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(Demo01ContactEntity::getId));
    }

}
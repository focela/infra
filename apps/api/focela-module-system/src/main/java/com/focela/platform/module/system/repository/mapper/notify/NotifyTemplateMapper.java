package com.focela.platform.module.system.repository.mapper.notify;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.module.system.repository.entity.notify.NotifyTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotifyTemplateMapper extends BaseMapperX<NotifyTemplateEntity> {

    default NotifyTemplateEntity selectByCode(String code) {
        return selectOne(NotifyTemplateEntity::getCode, code);
    }

    default PageResult<NotifyTemplateEntity> selectPage(NotifyTemplatePageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NotifyTemplateEntity>()
                .likeIfPresent(NotifyTemplateEntity::getCode, reqVO.getCode())
                .likeIfPresent(NotifyTemplateEntity::getName, reqVO.getName())
                .eqIfPresent(NotifyTemplateEntity::getStatus, reqVO.getStatus())
                .betweenIfPresent(NotifyTemplateEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NotifyTemplateEntity::getId));
    }

}

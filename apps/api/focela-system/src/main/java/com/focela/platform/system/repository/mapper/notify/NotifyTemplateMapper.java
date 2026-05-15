package com.focela.platform.system.repository.mapper.notify;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.system.entity.notify.NotifyTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotifyTemplateMapper extends BaseMapperX<NotifyTemplateEntity> {

    default NotifyTemplateEntity selectByCode(String code) {
        return selectOne(NotifyTemplateEntity::getCode, code);
    }

    default PageResult<NotifyTemplateEntity> selectPage(NotifyTemplatePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<NotifyTemplateEntity>()
                .likeIfPresent(NotifyTemplateEntity::getCode, request.getCode())
                .likeIfPresent(NotifyTemplateEntity::getName, request.getName())
                .eqIfPresent(NotifyTemplateEntity::getStatus, request.getStatus())
                .betweenIfPresent(NotifyTemplateEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(NotifyTemplateEntity::getId));
    }

}

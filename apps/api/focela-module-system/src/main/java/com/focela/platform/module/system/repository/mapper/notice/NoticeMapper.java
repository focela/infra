package com.focela.platform.module.system.repository.mapper.notice;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.module.system.repository.entity.notice.NoticeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeEntity> {

    default PageResult<NoticeEntity> selectPage(NoticePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<NoticeEntity>()
                .likeIfPresent(NoticeEntity::getTitle, request.getTitle())
                .eqIfPresent(NoticeEntity::getStatus, request.getStatus())
                .orderByDesc(NoticeEntity::getId));
    }

}

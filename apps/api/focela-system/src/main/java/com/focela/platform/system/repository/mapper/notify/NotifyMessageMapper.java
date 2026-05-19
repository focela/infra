package com.focela.platform.system.repository.mapper.notify;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.mybatis.core.query.QueryWrapperX;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessageMyPageRequest;
import com.focela.platform.system.controller.admin.notify.dto.message.NotifyMessagePageRequest;
import com.focela.platform.system.domain.entity.notify.NotifyMessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface NotifyMessageMapper extends BaseMapperX<NotifyMessageEntity> {

    default PageResult<NotifyMessageEntity> selectPage(NotifyMessagePageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<NotifyMessageEntity>()
                .eqIfPresent(NotifyMessageEntity::getUserId, request.getUserId())
                .eqIfPresent(NotifyMessageEntity::getUserType, request.getUserType())
                .likeIfPresent(NotifyMessageEntity::getTemplateCode, request.getTemplateCode())
                .eqIfPresent(NotifyMessageEntity::getTemplateType, request.getTemplateType())
                .betweenIfPresent(NotifyMessageEntity::getCreateTime, request.getCreateTime())
                .orderByDesc(NotifyMessageEntity::getId));
    }

    default PageResult<NotifyMessageEntity> selectPage(NotifyMessageMyPageRequest request, Long userId, Integer userType) {
        return selectPage(request, new LambdaQueryWrapperX<NotifyMessageEntity>()
                .eqIfPresent(NotifyMessageEntity::getReadStatus, request.getReadStatus())
                .betweenIfPresent(NotifyMessageEntity::getCreateTime, request.getCreateTime())
                .eq(NotifyMessageEntity::getUserId, userId)
                .eq(NotifyMessageEntity::getUserType, userType)
                .orderByDesc(NotifyMessageEntity::getId));
    }

    default int updateListRead(Collection<Long> ids, Long userId, Integer userType) {
        return update(new NotifyMessageEntity().setReadStatus(true).setReadTime(LocalDateTime.now()),
                new LambdaQueryWrapperX<NotifyMessageEntity>()
                        .in(NotifyMessageEntity::getId, ids)
                        .eq(NotifyMessageEntity::getUserId, userId)
                        .eq(NotifyMessageEntity::getUserType, userType)
                        .eq(NotifyMessageEntity::getReadStatus, false));
    }

    default int updateListRead(Long userId, Integer userType) {
        return update(new NotifyMessageEntity().setReadStatus(true).setReadTime(LocalDateTime.now()),
                new LambdaQueryWrapperX<NotifyMessageEntity>()
                        .eq(NotifyMessageEntity::getUserId, userId)
                        .eq(NotifyMessageEntity::getUserType, userType)
                        .eq(NotifyMessageEntity::getReadStatus, false));
    }

    default List<NotifyMessageEntity> selectUnreadListByUserIdAndUserType(Long userId, Integer userType, Integer size) {
        return selectList(new QueryWrapperX<NotifyMessageEntity>() // QueryWrapperX is required because we need to use the limitN clause
                .eq("user_id", userId)
                .eq("user_type", userType)
                .eq("read_status", false)
                .orderByDesc("id").limitN(size));
    }

    default Long selectUnreadCountByUserIdAndUserType(Long userId, Integer userType) {
        return selectCount(new LambdaQueryWrapperX<NotifyMessageEntity>()
                .eq(NotifyMessageEntity::getReadStatus, false)
                .eq(NotifyMessageEntity::getUserId, userId)
                .eq(NotifyMessageEntity::getUserType, userType));
    }

}

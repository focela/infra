package com.focela.platform.module.system.repository.mapper.notify;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.framework.mybatis.core.query.QueryWrapperX;
import com.focela.platform.module.system.controller.admin.notify.dto.message.NotifyMessageMyPageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.message.NotifyMessagePageRequest;
import com.focela.platform.module.system.repository.entity.notify.NotifyMessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface NotifyMessageMapper extends BaseMapperX<NotifyMessageEntity> {

    default PageResult<NotifyMessageEntity> selectPage(NotifyMessagePageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NotifyMessageEntity>()
                .eqIfPresent(NotifyMessageEntity::getUserId, reqVO.getUserId())
                .eqIfPresent(NotifyMessageEntity::getUserType, reqVO.getUserType())
                .likeIfPresent(NotifyMessageEntity::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(NotifyMessageEntity::getTemplateType, reqVO.getTemplateType())
                .betweenIfPresent(NotifyMessageEntity::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(NotifyMessageEntity::getId));
    }

    default PageResult<NotifyMessageEntity> selectPage(NotifyMessageMyPageRequest reqVO, Long userId, Integer userType) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NotifyMessageEntity>()
                .eqIfPresent(NotifyMessageEntity::getReadStatus, reqVO.getReadStatus())
                .betweenIfPresent(NotifyMessageEntity::getCreateTime, reqVO.getCreateTime())
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
        return selectList(new QueryWrapperX<NotifyMessageEntity>() // 由于要使用 limitN 语句，所以只能用 QueryWrapperX
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

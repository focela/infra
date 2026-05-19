package com.focela.platform.infra.repository.mapper.job;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.domain.entity.job.JobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Scheduled job Mapper
 */
@Mapper
public interface JobMapper extends BaseMapperX<JobEntity> {

    default JobEntity selectByHandlerName(String handlerName) {
        return selectOne(JobEntity::getHandlerName, handlerName);
    }

    default PageResult<JobEntity> selectPage(JobPageRequest request) {
        return selectPage(request, new LambdaQueryWrapperX<JobEntity>()
                .likeIfPresent(JobEntity::getName, request.getName())
                .eqIfPresent(JobEntity::getStatus, request.getStatus())
                .likeIfPresent(JobEntity::getHandlerName, request.getHandlerName())
                .orderByDesc(JobEntity::getId));
    }

}

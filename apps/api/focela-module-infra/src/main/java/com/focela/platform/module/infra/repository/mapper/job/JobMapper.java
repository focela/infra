package com.focela.platform.module.infra.repository.mapper.job;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.job.dto.job.JobPageRequest;
import com.focela.platform.module.infra.repository.entity.job.JobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper
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

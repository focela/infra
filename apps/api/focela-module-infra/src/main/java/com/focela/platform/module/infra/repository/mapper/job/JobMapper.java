package com.focela.platform.module.infra.repository.mapper.job;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.infra.controller.admin.job.dto.job.JobPageRequest;
import com.focela.platform.module.infra.repository.entity.job.JobEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface JobMapper extends BaseMapperX<JobEntity> {

    default JobEntity selectByHandlerName(String handlerName) {
        return selectOne(JobEntity::getHandlerName, handlerName);
    }

    default PageResult<JobEntity> selectPage(JobPageRequest reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<JobEntity>()
                .likeIfPresent(JobEntity::getName, reqVO.getName())
                .eqIfPresent(JobEntity::getStatus, reqVO.getStatus())
                .likeIfPresent(JobEntity::getHandlerName, reqVO.getHandlerName())
                .orderByDesc(JobEntity::getId));
    }

}

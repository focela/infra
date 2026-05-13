package com.focela.platform.module.system.repository.mapper.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.mybatis.core.mapper.BaseMapperX;
import com.focela.platform.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.focela.platform.module.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.module.system.repository.entity.logger.LoginLogEntity;
import com.focela.platform.module.system.enums.logger.LoginResultEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapperX<LoginLogEntity> {

    default PageResult<LoginLogEntity> selectPage(LoginLogPageRequest request) {
        LambdaQueryWrapperX<LoginLogEntity> query = new LambdaQueryWrapperX<LoginLogEntity>()
                .likeIfPresent(LoginLogEntity::getUserIp, request.getUserIp())
                .likeIfPresent(LoginLogEntity::getUsername, request.getUsername())
                .betweenIfPresent(LoginLogEntity::getCreateTime, request.getCreateTime());
        if (Boolean.TRUE.equals(request.getStatus())) {
            query.eq(LoginLogEntity::getResult, LoginResultEnum.SUCCESS.getResult());
        } else if (Boolean.FALSE.equals(request.getStatus())) {
            query.gt(LoginLogEntity::getResult, LoginResultEnum.SUCCESS.getResult());
        }
        query.orderByDesc(LoginLogEntity::getId); // 降序
        return selectPage(request, query);
    }

}

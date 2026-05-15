package com.focela.platform.system.service.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.system.entity.logger.LoginLogEntity;

import jakarta.validation.Valid;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志
     *
     * @param id 编号
     * @return 登录日志
     */
    LoginLogEntity getLoginLog(Long id);

    /**
     * 获得登录日志分页
     *
     * @param pageRequest 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageRequest pageRequest);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateRpcRequest reqDTO);

}

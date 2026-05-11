package com.focela.platform.module.system.service.logger;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.focela.platform.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.focela.platform.module.system.repository.entity.logger.LoginLogEntity;

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
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

}

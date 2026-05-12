package com.focela.platform.module.system.service.logger;

import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.focela.platform.module.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.module.system.repository.entity.logger.LoginLogEntity;
import com.focela.platform.module.system.repository.mapper.logger.LoginLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class DefaultLoginLogService implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public LoginLogEntity getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageRequest pageRequest) {
        return loginLogMapper.selectPage(pageRequest);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogEntity loginLog = BeanUtils.toBean(reqDTO, LoginLogEntity.class);
        loginLogMapper.insert(loginLog);
    }

}

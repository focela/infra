package com.focela.platform.module.system.service.logger;

import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.focela.platform.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
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
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public LoginLogEntity getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogEntity loginLog = BeanUtils.toBean(reqDTO, LoginLogEntity.class);
        loginLogMapper.insert(loginLog);
    }

}

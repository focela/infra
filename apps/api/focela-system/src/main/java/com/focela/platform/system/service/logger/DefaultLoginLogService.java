package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.system.domain.entity.logger.LoginLogEntity;
import com.focela.platform.system.repository.mapper.logger.LoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Login log Service implementation
 */
@Service
@Validated
@RequiredArgsConstructor
public class DefaultLoginLogService implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    public LoginLogEntity getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogEntity> getLoginLogPage(LoginLogPageRequest pageRequest) {
        return loginLogMapper.selectPage(pageRequest);
    }

    @Override
    public void createLoginLog(LoginLogCreateRpcRequest request) {
        LoginLogEntity loginLog = BeanUtils.toBean(request, LoginLogEntity.class);
        loginLogMapper.insert(loginLog);
    }

}

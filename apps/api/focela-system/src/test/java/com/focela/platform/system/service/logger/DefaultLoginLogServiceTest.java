package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.controller.admin.logger.request.login.LoginLogPageRequest;
import com.focela.platform.system.domain.entity.logger.LoginLogEntity;
import com.focela.platform.system.repository.mapper.logger.LoginLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildBetweenTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.system.enums.logger.LoginResultEnum.CAPTCHA_CODE_ERROR;
import static com.focela.platform.system.enums.logger.LoginResultEnum.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DefaultLoginLogService.class)
public class DefaultLoginLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultLoginLogService loginLogService;

    @Resource
    private LoginLogMapper loginLogMapper;

    @Test
    public void getLoginLogPage() {
        // mock data
        LoginLogEntity loginLogEntity = randomPojo(LoginLogEntity.class, o -> {
            o.setUserIp("192.168.199.16");
            o.setUsername("wang");
            o.setResult(SUCCESS.getResult());
            o.setCreateTime(buildTime(2021, 3, 6));
        });
        loginLogMapper.insert(loginLogEntity);
        // test status mismatch
        loginLogMapper.insert(cloneIgnoreId(loginLogEntity, o -> o.setResult(CAPTCHA_CODE_ERROR.getResult())));
        // test ip mismatch
        loginLogMapper.insert(cloneIgnoreId(loginLogEntity, o -> o.setUserIp("192.168.128.18")));
        // test username mismatch
        loginLogMapper.insert(cloneIgnoreId(loginLogEntity, o -> o.setUsername("focela_sample")));
        // test createTime mismatch
        loginLogMapper.insert(cloneIgnoreId(loginLogEntity, o -> o.setCreateTime(buildTime(2021, 2, 6))));
        // build call parameters
        LoginLogPageRequest request = new LoginLogPageRequest();
        request.setUsername("wang");
        request.setUserIp("192.168.199");
        request.setStatus(true);
        request.setCreateTime(buildBetweenTime(2021, 3, 5, 2021, 3, 7));

        // invoke
        PageResult<LoginLogEntity> pageResult = loginLogService.getLoginLogPage(request);
        // assert only one matching record was found
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(loginLogEntity, pageResult.getList().get(0));
    }

    @Test
    public void createLoginLog() {
        LoginLogCreateRpcRequest request = randomPojo(LoginLogCreateRpcRequest.class);

        // invoke
        loginLogService.createLoginLog(request);
        // assert
        LoginLogEntity loginLogEntity = loginLogMapper.selectOne(null);
        assertPojoEquals(request, loginLogEntity);
    }

}

package com.focela.platform.system.service.logger;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.system.api.logger.dto.LoginLogCreateRpcRequest;
import com.focela.platform.system.controller.admin.logger.dto.loginlog.LoginLogPageRequest;
import com.focela.platform.system.entity.logger.LoginLogEntity;
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
    public void testGetLoginLogPage() {
        // mock 数据
        LoginLogEntity loginLogDO = randomPojo(LoginLogEntity.class, o -> {
            o.setUserIp("192.168.199.16");
            o.setUsername("wang");
            o.setResult(SUCCESS.getResult());
            o.setCreateTime(buildTime(2021, 3, 6));
        });
        loginLogMapper.insert(loginLogDO);
        // 测试 status 不匹配
        loginLogMapper.insert(cloneIgnoreId(loginLogDO, o -> o.setResult(CAPTCHA_CODE_ERROR.getResult())));
        // 测试 ip 不匹配
        loginLogMapper.insert(cloneIgnoreId(loginLogDO, o -> o.setUserIp("192.168.128.18")));
        // 测试 username 不匹配
        loginLogMapper.insert(cloneIgnoreId(loginLogDO, o -> o.setUsername("yunai")));
        // 测试 createTime 不匹配
        loginLogMapper.insert(cloneIgnoreId(loginLogDO, o -> o.setCreateTime(buildTime(2021, 2, 6))));
        // 构造调用参数
        LoginLogPageRequest request = new LoginLogPageRequest();
        request.setUsername("wang");
        request.setUserIp("192.168.199");
        request.setStatus(true);
        request.setCreateTime(buildBetweenTime(2021, 3, 5, 2021, 3, 7));

        // 调用
        PageResult<LoginLogEntity> pageResult = loginLogService.getLoginLogPage(request);
        // 断言，只查到了一条符合条件的
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(loginLogDO, pageResult.getList().get(0));
    }

    @Test
    public void testCreateLoginLog() {
        LoginLogCreateRpcRequest reqDTO = randomPojo(LoginLogCreateRpcRequest.class);

        // 调用
        loginLogService.createLoginLog(reqDTO);
        // 断言
        LoginLogEntity loginLogDO = loginLogMapper.selectOne(null);
        assertPojoEquals(reqDTO, loginLogDO);
    }

}

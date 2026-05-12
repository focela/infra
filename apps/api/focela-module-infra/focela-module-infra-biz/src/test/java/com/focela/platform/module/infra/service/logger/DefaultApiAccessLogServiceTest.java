package com.focela.platform.module.infra.service.logger;

import com.focela.platform.framework.common.business.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.module.infra.controller.admin.logger.dto.apiaccesslog.ApiAccessLogPageRequest;
import com.focela.platform.module.infra.repository.entity.logger.ApiAccessLogEntity;
import com.focela.platform.module.infra.repository.mapper.logger.ApiAccessLogMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.List;

import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.*;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DefaultApiAccessLogService.class)
public class DefaultApiAccessLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultApiAccessLogService apiAccessLogService;

    @Resource
    private ApiAccessLogMapper apiAccessLogMapper;

    @Test
    public void testGetApiAccessLogPage() {
        ApiAccessLogEntity apiAccessLogDO = randomPojo(ApiAccessLogEntity.class, o -> {
            o.setUserId(2233L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setApplicationName("yudao-test");
            o.setRequestUrl("foo");
            o.setBeginTime(buildTime(2021, 3, 13));
            o.setDuration(1000);
            o.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());
        });
        apiAccessLogMapper.insert(apiAccessLogDO);
        // 测试 userId 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setUserId(3344L)));
        // 测试 userType 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 applicationName 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setApplicationName("test")));
        // 测试 requestUrl 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setRequestUrl("bar")));
        // 测试 beginTime 不匹配：构造一个早期时间 2021-02-06 00:00:00
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setBeginTime(buildTime(2021, 2, 6))));
        // 测试 duration 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setDuration(100)));
        // 测试 resultCode 不匹配
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogDO, o -> o.setResultCode(2)));
        // 准备参数
        ApiAccessLogPageRequest request = new ApiAccessLogPageRequest();
        request.setUserId(2233L);
        request.setUserType(UserTypeEnum.ADMIN.getValue());
        request.setApplicationName("yudao-test");
        request.setRequestUrl("foo");
        request.setBeginTime(buildBetweenTime(2021, 3, 13, 2021, 3, 13));
        request.setDuration(1000);
        request.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());

        // 调用
        PageResult<ApiAccessLogEntity> pageResult = apiAccessLogService.getApiAccessLogPage(request);
        // 断言，只查到了一条符合条件的
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(apiAccessLogDO, pageResult.getList().get(0));
    }

    @Test
    public void testCleanJobLog() {
        // mock 数据
        ApiAccessLogEntity log01 = randomPojo(ApiAccessLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-3))));
        apiAccessLogMapper.insert(log01);
        ApiAccessLogEntity log02 = randomPojo(ApiAccessLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-1))));
        apiAccessLogMapper.insert(log02);
        // 准备参数
        Integer exceedDay = 2;
        Integer deleteLimit = 1;

        // 调用
        Integer count = apiAccessLogService.cleanAccessLog(exceedDay, deleteLimit);
        // 断言
        assertEquals(1, count);
        List<ApiAccessLogEntity> logs = apiAccessLogMapper.selectList();
        assertEquals(1, logs.size());
        // TODO:  createTime updateTime 被屏蔽，仅 win11 会复现，建议后续修复。
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

    @Test
    public void testCreateApiAccessLog() {
        // 准备参数
        ApiAccessLogCreateReqDTO createDTO = randomPojo(ApiAccessLogCreateReqDTO.class);

        // 调用
        apiAccessLogService.createApiAccessLog(createDTO);
        // 断言
        ApiAccessLogEntity apiAccessLogDO = apiAccessLogMapper.selectOne(null);
        assertPojoEquals(createDTO, apiAccessLogDO);
    }

}

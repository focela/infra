package com.focela.platform.infra.service.logger;

import com.focela.platform.common.api.infra.logger.dto.ApiAccessLogCreateRpcRequest;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.exception.enums.GlobalErrorCodeConstants;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.logger.request.apiaccesslog.ApiAccessLogPageRequest;
import com.focela.platform.infra.domain.entity.logger.ApiAccessLogEntity;
import com.focela.platform.infra.repository.mapper.logger.ApiAccessLogMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.List;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.*;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DefaultApiAccessLogService.class)
public class DefaultApiAccessLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultApiAccessLogService apiAccessLogService;

    @Resource
    private ApiAccessLogMapper apiAccessLogMapper;

    @Test
    public void testGetApiAccessLogPage() {
        ApiAccessLogEntity apiAccessLogEntity = randomPojo(ApiAccessLogEntity.class, o -> {
            o.setUserId(2233L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setApplicationName("focela-test");
            o.setRequestUrl("foo");
            o.setBeginTime(buildTime(2021, 3, 13));
            o.setDuration(1000);
            o.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());
        });
        apiAccessLogMapper.insert(apiAccessLogEntity);
        // Test userId mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setUserId(3344L)));
        // Test userType mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // Test applicationName mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setApplicationName("test")));
        // Test requestUrl mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setRequestUrl("bar")));
        // Test beginTime mismatch: construct an earlier timestamp 2021-02-06 00:00:00
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setBeginTime(buildTime(2021, 2, 6))));
        // Test duration mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setDuration(100)));
        // Test resultCode mismatch
        apiAccessLogMapper.insert(cloneIgnoreId(apiAccessLogEntity, o -> o.setResultCode(2)));
        // Prepare parameters
        ApiAccessLogPageRequest request = new ApiAccessLogPageRequest();
        request.setUserId(2233L);
        request.setUserType(UserTypeEnum.ADMIN.getValue());
        request.setApplicationName("focela-test");
        request.setRequestUrl("foo");
        request.setBeginTime(buildBetweenTime(2021, 3, 13, 2021, 3, 13));
        request.setDuration(1000);
        request.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());

        // Invoke
        PageResult<ApiAccessLogEntity> pageResult = apiAccessLogService.getApiAccessLogPage(request);
        // Assert that only one matching record is returned
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(apiAccessLogEntity, pageResult.getList().get(0));
    }

    @Test
    public void testCleanJobLog() {
        // mock data
        ApiAccessLogEntity log01 = randomPojo(ApiAccessLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-3))));
        apiAccessLogMapper.insert(log01);
        ApiAccessLogEntity log02 = randomPojo(ApiAccessLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-1))));
        apiAccessLogMapper.insert(log02);
        // Prepare parameters
        Integer exceedDay = 2;
        Integer deleteLimit = 1;

        // Invoke
        Integer count = apiAccessLogService.cleanAccessLog(exceedDay, deleteLimit);
        // Assert
        assertEquals(1, count);
        List<ApiAccessLogEntity> logs = apiAccessLogMapper.selectList();
        assertEquals(1, logs.size());
        // TODO: createTime and updateTime are blocked; reproduces on win11 only — follow-up fix recommended.
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

    @Test
    public void testCreateApiAccessLog() {
        // Prepare parameters
        ApiAccessLogCreateRpcRequest createRequest = randomPojo(ApiAccessLogCreateRpcRequest.class);

        // Invoke
        apiAccessLogService.createApiAccessLog(createRequest);
        // Assert
        ApiAccessLogEntity apiAccessLogEntity = apiAccessLogMapper.selectOne(null);
        assertPojoEquals(createRequest, apiAccessLogEntity);
    }

}

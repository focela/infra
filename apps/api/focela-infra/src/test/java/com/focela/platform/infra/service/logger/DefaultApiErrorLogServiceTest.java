package com.focela.platform.infra.service.logger;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.entity.logger.ApiErrorLogEntity;
import com.focela.platform.infra.repository.mapper.logger.ApiErrorLogMapper;
import com.focela.platform.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.List;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.*;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static com.focela.platform.infra.constants.InfraErrorCodeConstants.API_ERROR_LOG_PROCESSED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(DefaultApiErrorLogService.class)
public class DefaultApiErrorLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultApiErrorLogService apiErrorLogService;

    @Resource
    private ApiErrorLogMapper apiErrorLogMapper;

    @Test
    public void testGetApiErrorLogPage() {
        // mock data
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class, o -> {
            o.setUserId(2233L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setApplicationName("focela-test");
            o.setRequestUrl("foo");
            o.setExceptionTime(buildTime(2021, 3, 13));
            o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        });
        apiErrorLogMapper.insert(apiErrorLogDO);
        // Test userId mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setUserId(3344L)));
        // Test userType mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // Test applicationName mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setApplicationName("test")));
        // Test requestUrl mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setRequestUrl("bar")));
        // Test exceptionTime mismatch: construct an earlier timestamp 2021-02-06 00:00:00
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setExceptionTime(buildTime(2021, 2, 6))));
        // Test progressStatus mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, logEntity -> logEntity.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus())));
        // Prepare parameters
        ApiErrorLogPageRequest request = new ApiErrorLogPageRequest();
        request.setUserId(2233L);
        request.setUserType(UserTypeEnum.ADMIN.getValue());
        request.setApplicationName("focela-test");
        request.setRequestUrl("foo");
        request.setExceptionTime(buildBetweenTime(2021, 3, 1, 2021, 3, 31));
        request.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());

        // Invoke
        PageResult<ApiErrorLogEntity> pageResult = apiErrorLogService.getApiErrorLogPage(request);
        // Assert that only one matching record is returned
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(apiErrorLogDO, pageResult.getList().get(0));
    }

    @Test
    public void testCreateApiErrorLog() {
        // Prepare parameters
        ApiErrorLogCreateRpcRequest createDTO = randomPojo(ApiErrorLogCreateRpcRequest.class);

        // Invoke
        apiErrorLogService.createApiErrorLog(createDTO);
        // Assert
        ApiErrorLogEntity apiErrorLogDO = apiErrorLogMapper.selectOne(null);
        assertPojoEquals(createDTO, apiErrorLogDO);
        assertEquals(ApiErrorLogProcessStatusEnum.INIT.getStatus(), apiErrorLogDO.getProcessStatus());
    }

    @Test
    public void testUpdateApiErrorLogProcess_success() {
        // Prepare parameters
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogDO);
        // Prepare parameters
        Long id = apiErrorLogDO.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // Invoke
        apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId);
        // Assert
        ApiErrorLogEntity dbApiErrorLogDO = apiErrorLogMapper.selectById(apiErrorLogDO.getId());
        assertEquals(processStatus, dbApiErrorLogDO.getProcessStatus());
        assertEquals(processUserId, dbApiErrorLogDO.getProcessUserId());
        assertNotNull(dbApiErrorLogDO.getProcessTime());
    }

    @Test
    public void testUpdateApiErrorLogProcess_processed() {
        // Prepare parameters
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogDO);
        // Prepare parameters
        Long id = apiErrorLogDO.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // Invoke and verify exception
        assertServiceException(() ->
                apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId),
                API_ERROR_LOG_PROCESSED);
    }

    @Test
    public void testUpdateApiErrorLogProcess_notFound() {
        // Prepare parameters
        Long id = randomLongId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // Invoke and verify exception
        assertServiceException(() ->
                apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId),
                API_ERROR_LOG_NOT_FOUND);
    }

    @Test
    public void testCleanJobLog() {
        // mock data
        ApiErrorLogEntity log01 = randomPojo(ApiErrorLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-3))));
        apiErrorLogMapper.insert(log01);
        ApiErrorLogEntity log02 = randomPojo(ApiErrorLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-1))));
        apiErrorLogMapper.insert(log02);
        // Prepare parameters
        Integer exceedDay = 2;
        Integer deleteLimit = 1;

        // Invoke
        Integer count = apiErrorLogService.cleanErrorLog(exceedDay, deleteLimit);
        // Assert
        assertEquals(1, count);
        List<ApiErrorLogEntity> logs = apiErrorLogMapper.selectList();
        assertEquals(1, logs.size());
        // TODO: createTime and updateTime are blocked; reproduces on win11 only — follow-up fix recommended.
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

}

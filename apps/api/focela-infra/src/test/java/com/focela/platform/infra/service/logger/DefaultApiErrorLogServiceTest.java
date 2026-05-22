package com.focela.platform.infra.service.logger;

import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.common.api.infra.logger.dto.ApiErrorLogCreateRpcRequest;
import com.focela.platform.infra.controller.admin.logger.request.errorlog.ApiErrorLogPageRequest;
import com.focela.platform.infra.domain.entity.logger.ApiErrorLogEntity;
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
    public void getApiErrorLogPage() {
        // mock data
        ApiErrorLogEntity apiErrorLogEntity = randomPojo(ApiErrorLogEntity.class, o -> {
            o.setUserId(2233L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setApplicationName("focela-test");
            o.setRequestUrl("foo");
            o.setExceptionTime(buildTime(2021, 3, 13));
            o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        });
        apiErrorLogMapper.insert(apiErrorLogEntity);
        // Test userId mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, o -> o.setUserId(3344L)));
        // Test userType mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // Test applicationName mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, o -> o.setApplicationName("test")));
        // Test requestUrl mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, o -> o.setRequestUrl("bar")));
        // Test exceptionTime mismatch: construct an earlier timestamp 2021-02-06 00:00:00
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, o -> o.setExceptionTime(buildTime(2021, 2, 6))));
        // Test progressStatus mismatch
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogEntity, logEntity -> logEntity.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus())));
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
        assertPojoEquals(apiErrorLogEntity, pageResult.getList().get(0));
    }

    @Test
    public void createApiErrorLog() {
        // Prepare parameters
        ApiErrorLogCreateRpcRequest createRequest = randomPojo(ApiErrorLogCreateRpcRequest.class);

        // Invoke
        apiErrorLogService.createApiErrorLog(createRequest);
        // Assert
        ApiErrorLogEntity apiErrorLogEntity = apiErrorLogMapper.selectOne(null);
        assertPojoEquals(createRequest, apiErrorLogEntity);
        assertEquals(ApiErrorLogProcessStatusEnum.INIT.getStatus(), apiErrorLogEntity.getProcessStatus());
    }

    @Test
    public void updateApiErrorLogProcess_success() {
        // Prepare parameters
        ApiErrorLogEntity apiErrorLogEntity = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogEntity);
        // Prepare parameters
        Long id = apiErrorLogEntity.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // Invoke
        apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId);
        // Assert
        ApiErrorLogEntity dbApiErrorLogEntity = apiErrorLogMapper.selectById(apiErrorLogEntity.getId());
        assertEquals(processStatus, dbApiErrorLogEntity.getProcessStatus());
        assertEquals(processUserId, dbApiErrorLogEntity.getProcessUserId());
        assertNotNull(dbApiErrorLogEntity.getProcessTime());
    }

    @Test
    public void updateApiErrorLogProcess_processed() {
        // Prepare parameters
        ApiErrorLogEntity apiErrorLogEntity = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogEntity);
        // Prepare parameters
        Long id = apiErrorLogEntity.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // Invoke and verify exception
        assertServiceException(() ->
                apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId),
                API_ERROR_LOG_PROCESSED);
    }

    @Test
    public void updateApiErrorLogProcess_missing() {
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
    public void cleanJobLog() {
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
        // Known fixture gap: createTime and updateTime are excluded because this assertion is unstable on Windows 11.
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

}

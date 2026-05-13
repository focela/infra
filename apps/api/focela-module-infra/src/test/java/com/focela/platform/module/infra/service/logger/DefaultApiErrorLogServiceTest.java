package com.focela.platform.module.infra.service.logger;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.test.core.support.BaseDbUnitTest;
import com.focela.platform.framework.common.business.infra.logger.dto.ApiErrorLogCreateReqDTO;
import com.focela.platform.module.infra.controller.admin.logger.dto.apierrorlog.ApiErrorLogPageRequest;
import com.focela.platform.module.infra.entity.logger.ApiErrorLogEntity;
import com.focela.platform.module.infra.repository.mapper.logger.ApiErrorLogMapper;
import com.focela.platform.module.infra.enums.logger.ApiErrorLogProcessStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.List;

import static cn.hutool.core.util.RandomUtil.randomEle;
import static com.focela.platform.framework.common.utils.date.LocalDateTimeUtils.*;
import static com.focela.platform.framework.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.framework.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomLongId;
import static com.focela.platform.framework.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.module.infra.constants.ErrorCodeConstants.API_ERROR_LOG_NOT_FOUND;
import static com.focela.platform.module.infra.constants.ErrorCodeConstants.API_ERROR_LOG_PROCESSED;
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
        // mock 数据
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class, o -> {
            o.setUserId(2233L);
            o.setUserType(UserTypeEnum.ADMIN.getValue());
            o.setApplicationName("yudao-test");
            o.setRequestUrl("foo");
            o.setExceptionTime(buildTime(2021, 3, 13));
            o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());
        });
        apiErrorLogMapper.insert(apiErrorLogDO);
        // 测试 userId 不匹配
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setUserId(3344L)));
        // 测试 userType 不匹配
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setUserType(UserTypeEnum.MEMBER.getValue())));
        // 测试 applicationName 不匹配
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setApplicationName("test")));
        // 测试 requestUrl 不匹配
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setRequestUrl("bar")));
        // 测试 exceptionTime 不匹配：构造一个早期时间 2021-02-06 00:00:00
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, o -> o.setExceptionTime(buildTime(2021, 2, 6))));
        // 测试 progressStatus 不匹配
        apiErrorLogMapper.insert(cloneIgnoreId(apiErrorLogDO, logEntity -> logEntity.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus())));
        // 准备参数
        ApiErrorLogPageRequest request = new ApiErrorLogPageRequest();
        request.setUserId(2233L);
        request.setUserType(UserTypeEnum.ADMIN.getValue());
        request.setApplicationName("yudao-test");
        request.setRequestUrl("foo");
        request.setExceptionTime(buildBetweenTime(2021, 3, 1, 2021, 3, 31));
        request.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus());

        // 调用
        PageResult<ApiErrorLogEntity> pageResult = apiErrorLogService.getApiErrorLogPage(request);
        // 断言，只查到了一条符合条件的
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(apiErrorLogDO, pageResult.getList().get(0));
    }

    @Test
    public void testCreateApiErrorLog() {
        // 准备参数
        ApiErrorLogCreateReqDTO createDTO = randomPojo(ApiErrorLogCreateReqDTO.class);

        // 调用
        apiErrorLogService.createApiErrorLog(createDTO);
        // 断言
        ApiErrorLogEntity apiErrorLogDO = apiErrorLogMapper.selectOne(null);
        assertPojoEquals(createDTO, apiErrorLogDO);
        assertEquals(ApiErrorLogProcessStatusEnum.INIT.getStatus(), apiErrorLogDO.getProcessStatus());
    }

    @Test
    public void testUpdateApiErrorLogProcess_success() {
        // 准备参数
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogDO);
        // 准备参数
        Long id = apiErrorLogDO.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // 调用
        apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId);
        // 断言
        ApiErrorLogEntity dbApiErrorLogDO = apiErrorLogMapper.selectById(apiErrorLogDO.getId());
        assertEquals(processStatus, dbApiErrorLogDO.getProcessStatus());
        assertEquals(processUserId, dbApiErrorLogDO.getProcessUserId());
        assertNotNull(dbApiErrorLogDO.getProcessTime());
    }

    @Test
    public void testUpdateApiErrorLogProcess_processed() {
        // 准备参数
        ApiErrorLogEntity apiErrorLogDO = randomPojo(ApiErrorLogEntity.class,
                o -> o.setProcessStatus(ApiErrorLogProcessStatusEnum.DONE.getStatus()));
        apiErrorLogMapper.insert(apiErrorLogDO);
        // 准备参数
        Long id = apiErrorLogDO.getId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // 调用，并断言异常
        assertServiceException(() ->
                apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId),
                API_ERROR_LOG_PROCESSED);
    }

    @Test
    public void testUpdateApiErrorLogProcess_notFound() {
        // 准备参数
        Long id = randomLongId();
        Integer processStatus = randomEle(ApiErrorLogProcessStatusEnum.values()).getStatus();
        Long processUserId = randomLongId();

        // 调用，并断言异常
        assertServiceException(() ->
                apiErrorLogService.updateApiErrorLogProcess(id, processStatus, processUserId),
                API_ERROR_LOG_NOT_FOUND);
    }

    @Test
    public void testCleanJobLog() {
        // mock 数据
        ApiErrorLogEntity log01 = randomPojo(ApiErrorLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-3))));
        apiErrorLogMapper.insert(log01);
        ApiErrorLogEntity log02 = randomPojo(ApiErrorLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-1))));
        apiErrorLogMapper.insert(log02);
        // 准备参数
        Integer exceedDay = 2;
        Integer deleteLimit = 1;

        // 调用
        Integer count = apiErrorLogService.cleanErrorLog(exceedDay, deleteLimit);
        // 断言
        assertEquals(1, count);
        List<ApiErrorLogEntity> logs = apiErrorLogMapper.selectList();
        assertEquals(1, logs.size());
        // TODO:  createTime updateTime 被屏蔽，仅 win11 会复现，建议后续修复。
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

}

package com.focela.platform.infra.service.job;

import com.focela.platform.common.model.PageResult;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.job.dto.log.JobLogPageRequest;
import com.focela.platform.infra.entity.job.JobLogEntity;
import com.focela.platform.infra.repository.mapper.job.JobLogMapper;
import com.focela.platform.infra.enums.job.JobLogStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.focela.platform.common.utils.date.LocalDateTimeUtils.addTime;
import static com.focela.platform.common.utils.date.LocalDateTimeUtils.buildTime;
import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(DefaultJobLogService.class)
public class DefaultJobLogServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultJobLogService jobLogService;
    @Resource
    private JobLogMapper jobLogMapper;

    @Test
    public void testCreateJobLog() {
        // Prepare parameters
        JobLogEntity request = randomPojo(JobLogEntity.class, o -> o.setExecuteIndex(1));

        // Invoke
        Long id = jobLogService.createJobLog(request.getJobId(), request.getBeginTime(),
                request.getHandlerName(), request.getHandlerParam(), request.getExecuteIndex());
        // Assert
        assertNotNull(id);
        // Verify record properties are correct
        JobLogEntity job = jobLogMapper.selectById(id);
        assertEquals(JobLogStatusEnum.RUNNING.getStatus(), job.getStatus());
    }

    @Test
    public void testUpdateJobLogResultAsync_success() {
        // mock data
        JobLogEntity log = randomPojo(JobLogEntity.class, o -> {
            o.setExecuteIndex(1);
            o.setStatus(JobLogStatusEnum.RUNNING.getStatus());
        });
        jobLogMapper.insert(log);
        // Prepare parameters
        Long logId = log.getId();
        LocalDateTime endTime = randomLocalDateTime();
        Integer duration = randomInteger();
        boolean success = true;
        String result = randomString();

        // Invoke
        jobLogService.updateJobLogResultAsync(logId, endTime, duration, success, result);
        // Verify record properties are correct
        JobLogEntity dbLog = jobLogMapper.selectById(log.getId());
        assertEquals(endTime, dbLog.getEndTime());
        assertEquals(duration, dbLog.getDuration());
        assertEquals(JobLogStatusEnum.SUCCESS.getStatus(), dbLog.getStatus());
        assertEquals(result, dbLog.getResult());
    }

    @Test
    public void testUpdateJobLogResultAsync_failure() {
        // mock data
        JobLogEntity log = randomPojo(JobLogEntity.class, o -> {
            o.setExecuteIndex(1);
            o.setStatus(JobLogStatusEnum.RUNNING.getStatus());
        });
        jobLogMapper.insert(log);
        // Prepare parameters
        Long logId = log.getId();
        LocalDateTime endTime = randomLocalDateTime();
        Integer duration = randomInteger();
        boolean success = false;
        String result = randomString();

        // Invoke
        jobLogService.updateJobLogResultAsync(logId, endTime, duration, success, result);
        // Verify record properties are correct
        JobLogEntity dbLog = jobLogMapper.selectById(log.getId());
        assertEquals(endTime, dbLog.getEndTime());
        assertEquals(duration, dbLog.getDuration());
        assertEquals(JobLogStatusEnum.FAILURE.getStatus(), dbLog.getStatus());
        assertEquals(result, dbLog.getResult());
    }

    @Test
    public void testCleanJobLog() {
        // mock data
        JobLogEntity log01 = randomPojo(JobLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-3))))
                .setExecuteIndex(1);
        jobLogMapper.insert(log01);
        JobLogEntity log02 = randomPojo(JobLogEntity.class, o -> o.setCreateTime(addTime(Duration.ofDays(-1))))
                .setExecuteIndex(1);
        jobLogMapper.insert(log02);
        // Prepare parameters
        Integer exceedDay = 2;
        Integer deleteLimit = 1;

        // Invoke
        Integer count = jobLogService.cleanJobLog(exceedDay, deleteLimit);
        // Assert
        assertEquals(1, count);
        List<JobLogEntity> logs = jobLogMapper.selectList();
        assertEquals(1, logs.size());
        // TODO: createTime and updateTime are blocked; reproduces on win11 only — follow-up fix recommended.
        assertPojoEquals(log02, logs.get(0), "createTime", "updateTime");
    }

    @Test
    public void testGetJobLog() {
        // mock data
        JobLogEntity dbJobLog = randomPojo(JobLogEntity.class, o -> o.setExecuteIndex(1));
        jobLogMapper.insert(dbJobLog);
        // Prepare parameters
        Long id = dbJobLog.getId();

        // Invoke
        JobLogEntity jobLog = jobLogService.getJobLog(id);
        // Assert
        assertPojoEquals(dbJobLog, jobLog);
    }

    @Test
    public void testGetJobPage() {
        // mock data
        JobLogEntity dbJobLog = randomPojo(JobLogEntity.class, o -> {
            o.setExecuteIndex(1);
            o.setHandlerName("handlerName unit test");
            o.setStatus(JobLogStatusEnum.SUCCESS.getStatus());
            o.setBeginTime(buildTime(2021, 1, 8));
            o.setEndTime(buildTime(2021, 1, 8));
        });
        jobLogMapper.insert(dbJobLog);
        // Test jobId mismatch
        jobLogMapper.insert(cloneIgnoreId(dbJobLog, o -> o.setJobId(randomLongId())));
        // Test handlerName mismatch
        jobLogMapper.insert(cloneIgnoreId(dbJobLog, o -> o.setHandlerName(randomString())));
        // Test beginTime mismatch
        jobLogMapper.insert(cloneIgnoreId(dbJobLog, o -> o.setBeginTime(buildTime(2021, 1, 7))));
        // Test endTime mismatch
        jobLogMapper.insert(cloneIgnoreId(dbJobLog, o -> o.setEndTime(buildTime(2021, 1, 9))));
        // Test status mismatch
        jobLogMapper.insert(cloneIgnoreId(dbJobLog, o -> o.setStatus(JobLogStatusEnum.FAILURE.getStatus())));
        // Prepare parameters
        JobLogPageRequest reqVo = new JobLogPageRequest();
        reqVo.setJobId(dbJobLog.getJobId());
        reqVo.setHandlerName("unit");
        reqVo.setBeginTime(dbJobLog.getBeginTime());
        reqVo.setEndTime(dbJobLog.getEndTime());
        reqVo.setStatus(JobLogStatusEnum.SUCCESS.getStatus());

        // Invoke
        PageResult<JobLogEntity> pageResult = jobLogService.getJobLogPage(reqVo);
        // Assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbJobLog, pageResult.getList().get(0));
    }

}

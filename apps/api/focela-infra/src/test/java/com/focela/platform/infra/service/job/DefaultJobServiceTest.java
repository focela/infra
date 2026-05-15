package com.focela.platform.infra.service.job;

import cn.hutool.extra.spring.SpringUtil;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.quartz.core.scheduler.SchedulerManager;
import com.focela.platform.test.core.support.BaseDbUnitTest;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.JobSaveRequest;
import com.focela.platform.infra.entity.job.JobEntity;
import com.focela.platform.infra.repository.mapper.job.JobMapper;
import com.focela.platform.infra.enums.job.JobStatusEnum;
import com.focela.platform.infra.job.JobLogCleanJob;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.focela.platform.common.utils.object.ObjectUtils.cloneIgnoreId;
import static com.focela.platform.test.core.utils.AssertUtils.assertPojoEquals;
import static com.focela.platform.test.core.utils.AssertUtils.assertServiceException;
import static com.focela.platform.test.core.utils.RandomUtils.randomPojo;
import static com.focela.platform.test.core.utils.RandomUtils.randomString;
import static com.focela.platform.infra.constants.ErrorCodeConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@Import(DefaultJobService.class)
public class DefaultJobServiceTest extends BaseDbUnitTest {

    @Resource
    private DefaultJobService jobService;
    @Resource
    private JobMapper jobMapper;
    @MockitoBean
    private SchedulerManager schedulerManager;

    @MockitoBean
    private JobLogCleanJob jobLogCleanJob;

    @Test
    public void testCreateJob_cronExpressionValid() {
        // Prepare parameters. Cron expression is a String; default is a random string.
        JobSaveRequest request = randomPojo(JobSaveRequest.class);

        // Invoke and verify exception
        assertServiceException(() -> jobService.createJob(request), JOB_CRON_EXPRESSION_VALID);
    }

    @Test
    public void testCreateJob_jobHandlerExists() throws SchedulerException {
        // Prepare parameters with the Cron expression set
        JobSaveRequest request = randomPojo(JobSaveRequest.class, o -> o.setCronExpression("0 0/1 * * * ? *"));
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(request.getHandlerName())))
                    .thenReturn(jobLogCleanJob);

            // Invoke
            jobService.createJob(request);
            // Invoke and verify exception
            assertServiceException(() -> jobService.createJob(request), JOB_HANDLER_EXISTS);
        }
    }

    @Test
    public void testCreateJob_success() throws SchedulerException {
        // Prepare parameters with the Cron expression set
        JobSaveRequest request = randomPojo(JobSaveRequest.class, o -> o.setCronExpression("0 0/1 * * * ? *"))
                .setId(null);
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(request.getHandlerName())))
                    .thenReturn(jobLogCleanJob);

            // Invoke
            Long jobId = jobService.createJob(request);
            // Assert
            assertNotNull(jobId);
            // Verify record properties are correct
            JobEntity job = jobMapper.selectById(jobId);
            assertPojoEquals(request, job, "id");
            assertEquals(JobStatusEnum.NORMAL.getStatus(), job.getStatus());
            // Verify the call
            verify(schedulerManager).addJob(eq(job.getId()), eq(job.getHandlerName()), eq(job.getHandlerParam()),
                    eq(job.getCronExpression()), eq(request.getRetryCount()), eq(request.getRetryInterval()));
        }
    }

    @Test
    public void testUpdateJob_jobNotExists(){
        // Prepare parameters
        JobSaveRequest request = randomPojo(JobSaveRequest.class, o -> o.setCronExpression("0 0/1 * * * ? *"));

        // Invoke and verify exception
        assertServiceException(() -> jobService.updateJob(request), JOB_NOT_EXISTS);
    }

    @Test
    public void testUpdateJob_onlyNormalStatus(){
        // mock data
        JobEntity job = randomPojo(JobEntity.class, o -> o.setStatus(JobStatusEnum.INIT.getStatus()));
        jobMapper.insert(job);
        // Prepare parameters
        JobSaveRequest updateRequest = randomPojo(JobSaveRequest.class, o -> {
            o.setId(job.getId());
            o.setCronExpression("0 0/1 * * * ? *");
        });

        // Invoke and verify exception
        assertServiceException(() -> jobService.updateJob(updateRequest),
                JOB_UPDATE_ONLY_NORMAL_STATUS);
    }

    @Test
    public void testUpdateJob_success() throws SchedulerException {
        // mock data
        JobEntity job = randomPojo(JobEntity.class, o -> o.setStatus(JobStatusEnum.NORMAL.getStatus()));
        jobMapper.insert(job);
        // Prepare parameters
        JobSaveRequest updateRequest = randomPojo(JobSaveRequest.class, o -> {
            o.setId(job.getId());
            o.setCronExpression("0 0/1 * * * ? *");
        });
        try (MockedStatic<SpringUtil> springUtilMockedStatic = mockStatic(SpringUtil.class)) {
            springUtilMockedStatic.when(() -> SpringUtil.getBean(eq(updateRequest.getHandlerName())))
                    .thenReturn(jobLogCleanJob);

            // Invoke
            jobService.updateJob(updateRequest);
            // Verify record properties are correct
            JobEntity updateJob = jobMapper.selectById(updateRequest.getId());
            assertPojoEquals(updateRequest, updateJob);
            // Verify the call
            verify(schedulerManager).updateJob(eq(job.getHandlerName()), eq(updateRequest.getHandlerParam()),
                    eq(updateRequest.getCronExpression()), eq(updateRequest.getRetryCount()), eq(updateRequest.getRetryInterval()));
        }
    }

    @Test
    public void testUpdateJobStatus_changeStatusInvalid() {
        // Invoke and verify exception
        assertServiceException(() -> jobService.updateJobStatus(1L, JobStatusEnum.INIT.getStatus()),
                JOB_CHANGE_STATUS_INVALID);
    }

    @Test
    public void testUpdateJobStatus_changeStatusEquals() {
        // mock data
        JobEntity job = randomPojo(JobEntity.class, o -> o.setStatus(JobStatusEnum.NORMAL.getStatus()));
        jobMapper.insert(job);

        // Invoke and verify exception
        assertServiceException(() -> jobService.updateJobStatus(job.getId(), job.getStatus()),
                JOB_CHANGE_STATUS_EQUALS);
    }

    @Test
    public void testUpdateJobStatus_stopSuccess() throws SchedulerException {
        // mock data
        JobEntity job = randomPojo(JobEntity.class, o -> o.setStatus(JobStatusEnum.NORMAL.getStatus()));
        jobMapper.insert(job);

        // Invoke
        jobService.updateJobStatus(job.getId(), JobStatusEnum.STOP.getStatus());
        // Verify record properties are correct
        JobEntity dbJob = jobMapper.selectById(job.getId());
        assertEquals(JobStatusEnum.STOP.getStatus(), dbJob.getStatus());
        // Verify the call
        verify(schedulerManager).pauseJob(eq(job.getHandlerName()));
    }

    @Test
    public void testUpdateJobStatus_normalSuccess() throws SchedulerException {
        // mock data
        JobEntity job = randomPojo(JobEntity.class, o -> o.setStatus(JobStatusEnum.STOP.getStatus()));
        jobMapper.insert(job);

        // Invoke
        jobService.updateJobStatus(job.getId(), JobStatusEnum.NORMAL.getStatus());
        // Verify record properties are correct
        JobEntity dbJob = jobMapper.selectById(job.getId());
        assertEquals(JobStatusEnum.NORMAL.getStatus(), dbJob.getStatus());
        // Verify the call
        verify(schedulerManager).resumeJob(eq(job.getHandlerName()));
    }

    @Test
    public void testTriggerJob_success() throws SchedulerException {
        // mock data
        JobEntity job = randomPojo(JobEntity.class);
        jobMapper.insert(job);

        // Invoke
        jobService.triggerJob(job.getId());
        // Verify the call
        verify(schedulerManager).triggerJob(eq(job.getId()),
                eq(job.getHandlerName()), eq(job.getHandlerParam()));
    }

    @Test
    public void testDeleteJob_success() throws SchedulerException {
        // mock data
        JobEntity job = randomPojo(JobEntity.class);
        jobMapper.insert(job);

        // Invoke
        jobService.deleteJob(job.getId());
        // Verify it no longer exists
        assertNull(jobMapper.selectById(job.getId()));
        // Verify the call
        verify(schedulerManager).deleteJob(eq(job.getHandlerName()));
    }

    @Test
    public void testGetJobPage() {
        // mock data
        JobEntity dbJob = randomPojo(JobEntity.class, o -> {
            o.setName("scheduled job test");
            o.setHandlerName("handlerName unit test");
            o.setStatus(JobStatusEnum.INIT.getStatus());
        });
        jobMapper.insert(dbJob);
        // Test name mismatch
        jobMapper.insert(cloneIgnoreId(dbJob, o -> o.setName("potato")));
        // Test status mismatch
        jobMapper.insert(cloneIgnoreId(dbJob, o -> o.setStatus(JobStatusEnum.NORMAL.getStatus())));
        // Test handlerName mismatch
        jobMapper.insert(cloneIgnoreId(dbJob, o -> o.setHandlerName(randomString())));
        // Prepare parameters
        JobPageRequest reqVo = new JobPageRequest();
        reqVo.setName("scheduled");
        reqVo.setStatus(JobStatusEnum.INIT.getStatus());
        reqVo.setHandlerName("unit");

        // Invoke
        PageResult<JobEntity> pageResult = jobService.getJobPage(reqVo);
        // Assert
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertPojoEquals(dbJob, pageResult.getList().get(0));
    }

    @Test
    public void testGetJob() {
        // mock data
        JobEntity dbJob = randomPojo(JobEntity.class);
        jobMapper.insert(dbJob);
        // Invoke
        JobEntity job = jobService.getJob(dbJob.getId());
        // Assert
        assertPojoEquals(dbJob, job);
    }

}

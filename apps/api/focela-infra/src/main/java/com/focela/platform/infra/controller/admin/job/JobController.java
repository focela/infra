package com.focela.platform.infra.controller.admin.job;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.framework.quartz.core.utils.CronUtils;
import com.focela.platform.infra.controller.admin.job.dto.JobPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.JobResponse;
import com.focela.platform.infra.controller.admin.job.dto.JobSaveRequest;
import com.focela.platform.infra.entity.job.JobEntity;
import com.focela.platform.infra.service.job.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - Scheduled job")
@RestController
@RequestMapping("/infra/job")
@Validated
public class JobController {

    @Resource
    private JobService jobService;

    @PostMapping("/create")
    @Operation(summary = "create scheduled job")
    @PreAuthorize("@ss.hasPermission('infra:job:create')")
    public CommonResult<Long> createJob(@Valid @RequestBody JobSaveRequest createRequest)
            throws SchedulerException {
        return success(jobService.createJob(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update scheduled job")
    @PreAuthorize("@ss.hasPermission('infra:job:update')")
    public CommonResult<Boolean> updateJob(@Valid @RequestBody JobSaveRequest updateRequest)
            throws SchedulerException {
        jobService.updateJob(updateRequest);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "update scheduled job status")
    @Parameters({
            @Parameter(name = "id", description = "ID", required = true, example = "1024"),
            @Parameter(name = "status", description = "Status", required = true, example = "1"),
    })
    @PreAuthorize("@ss.hasPermission('infra:job:update')")
    public CommonResult<Boolean> updateJobStatus(@RequestParam(value = "id") Long id, @RequestParam("status") Integer status)
            throws SchedulerException {
        jobService.updateJobStatus(id, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete scheduled job")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:delete')")
    public CommonResult<Boolean> deleteJob(@RequestParam("id") Long id)
            throws SchedulerException {
        jobService.deleteJob(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete scheduled job")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('infra:job:delete')")
    public CommonResult<Boolean> deleteJobList(@RequestParam("ids") List<Long> ids)
            throws SchedulerException {
        jobService.deleteJobList(ids);
        return success(true);
    }

    @PutMapping("/trigger")
    @Operation(summary = "trigger scheduled job")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:trigger')")
    public CommonResult<Boolean> triggerJob(@RequestParam("id") Long id) throws SchedulerException {
        jobService.triggerJob(id);
        return success(true);
    }

    @PostMapping("/sync")
    @Operation(summary = "sync scheduled job")
    @PreAuthorize("@ss.hasPermission('infra:job:create')")
    public CommonResult<Boolean> syncJob() throws SchedulerException {
        jobService.syncJob();
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get scheduled job")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<JobResponse> getJob(@RequestParam("id") Long id) {
        JobEntity job = jobService.getJob(id);
        return success(BeanUtils.toBean(job, JobResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get scheduled job page")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<PageResult<JobResponse>> getJobPage(@Valid JobPageRequest pageVO) {
        PageResult<JobEntity> pageResult = jobService.getJobPage(pageVO);
        return success(BeanUtils.toBean(pageResult, JobResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export scheduled job Excel")
    @PreAuthorize("@ss.hasPermission('infra:job:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportJobExcel(@Valid JobPageRequest exportRequest,
                               HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<JobEntity> list = jobService.getJobPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "定时任务.xls", "数据", JobResponse.class,
                BeanUtils.toBean(list, JobResponse.class));
    }

    @GetMapping("/get_next_times")
    @Operation(summary = "get scheduled job next n time execution time")
    @Parameters({
            @Parameter(name = "id", description = "ID", required = true, example = "1024"),
            @Parameter(name = "count", description = "count", example = "5")
    })
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<List<LocalDateTime>> getJobNextTimes(
            @RequestParam("id") Long id,
            @RequestParam(value = "count", required = false, defaultValue = "5") Integer count) {
        JobEntity job = jobService.getJob(id);
        if (job == null) {
            return success(Collections.emptyList());
        }
        return success(CronUtils.getNextTimes(job.getCronExpression(), count));
    }

}

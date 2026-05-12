package com.focela.platform.module.infra.controller.admin.job;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.pojo.CommonResult;
import com.focela.platform.framework.common.pojo.PageParam;
import com.focela.platform.framework.common.pojo.PageResult;
import com.focela.platform.framework.common.util.object.BeanUtils;
import com.focela.platform.framework.excel.core.util.ExcelUtils;
import com.focela.platform.module.infra.controller.admin.job.dto.log.JobLogPageRequest;
import com.focela.platform.module.infra.controller.admin.job.dto.log.JobLogResponse;
import com.focela.platform.module.infra.repository.entity.job.JobLogEntity;
import com.focela.platform.module.infra.service.job.JobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 定时任务日志")
@RestController
@RequestMapping("/infra/job-log")
@Validated
public class JobLogController {

    @Resource
    private JobLogService jobLogService;

    @GetMapping("/get")
    @Operation(summary = "获得定时任务日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<JobLogResponse> getJobLog(@RequestParam("id") Long id) {
        JobLogEntity jobLog = jobLogService.getJobLog(id);
        return success(BeanUtils.toBean(jobLog, JobLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得定时任务日志分页")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<PageResult<JobLogResponse>> getJobLogPage(@Valid JobLogPageRequest pageVO) {
        PageResult<JobLogEntity> pageResult = jobLogService.getJobLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, JobLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出定时任务日志 Excel")
    @PreAuthorize("@ss.hasPermission('infra:job:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportJobLogExcel(@Valid JobLogPageRequest exportRequest,
                                  HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<JobLogEntity> list = jobLogService.getJobLogPage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "任务日志.xls", "数据", JobLogResponse.class,
                BeanUtils.toBean(list, JobLogResponse.class));
    }

}
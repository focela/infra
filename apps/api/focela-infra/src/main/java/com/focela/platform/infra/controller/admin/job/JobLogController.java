package com.focela.platform.infra.controller.admin.job;

import com.focela.platform.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageParam;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.excel.core.utils.ExcelUtils;
import com.focela.platform.infra.controller.admin.job.dto.log.JobLogPageRequest;
import com.focela.platform.infra.controller.admin.job.dto.log.JobLogResponse;
import com.focela.platform.infra.entity.job.JobLogEntity;
import com.focela.platform.infra.service.job.JobLogService;
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

import static com.focela.platform.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - scheduled job log")
@RestController
@RequestMapping("/infra/job-log")
@Validated
public class JobLogController {

    @Resource
    private JobLogService jobLogService;

    @GetMapping("/get")
    @Operation(summary = "get scheduled job log")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<JobLogResponse> getJobLog(@RequestParam("id") Long id) {
        JobLogEntity jobLog = jobLogService.getJobLog(id);
        return success(BeanUtils.toBean(jobLog, JobLogResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get scheduled job log page")
    @PreAuthorize("@ss.hasPermission('infra:job:query')")
    public CommonResult<PageResult<JobLogResponse>> getJobLogPage(@Valid JobLogPageRequest pageVO) {
        PageResult<JobLogEntity> pageResult = jobLogService.getJobLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, JobLogResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export scheduled job log Excel")
    @PreAuthorize("@ss.hasPermission('infra:job:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportJobLogExcel(@Valid JobLogPageRequest exportRequest,
                                  HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<JobLogEntity> list = jobLogService.getJobLogPage(exportRequest).getList();
        // Export Excel
        ExcelUtils.write(response, "Job log.xls", "Data", JobLogResponse.class,
                BeanUtils.toBean(list, JobLogResponse.class));
    }

}
package com.focela.platform.system.controller.admin.sms;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageParam;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.framework.excel.core.utils.ExcelUtils;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplatePageRequest;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplateResponse;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplateSaveRequest;
import com.focela.platform.system.controller.admin.sms.dto.template.SmsTemplateSendRequest;
import com.focela.platform.system.entity.sms.SmsTemplateEntity;
import com.focela.platform.system.service.sms.SmsSendService;
import com.focela.platform.system.service.sms.SmsTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.focela.platform.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - SMS template")
@RestController
@RequestMapping("/system/sms-template")
public class SmsTemplateController {

    @Resource
    private SmsTemplateService smsTemplateService;
    @Resource
    private SmsSendService smsSendService;

    @PostMapping("/create")
    @Operation(summary = "create SMS template")
    @PreAuthorize("@ss.hasPermission('system:sms-template:create')")
    public CommonResult<Long> createSmsTemplate(@Valid @RequestBody SmsTemplateSaveRequest createRequest) {
        return success(smsTemplateService.createSmsTemplate(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update SMS template")
    @PreAuthorize("@ss.hasPermission('system:sms-template:update')")
    public CommonResult<Boolean> updateSmsTemplate(@Valid @RequestBody SmsTemplateSaveRequest updateRequest) {
        smsTemplateService.updateSmsTemplate(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete SMS template")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-template:delete')")
    public CommonResult<Boolean> deleteSmsTemplate(@RequestParam("id") Long id) {
        smsTemplateService.deleteSmsTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "ID list", required = true)
    @Operation(summary = "batch delete SMS template")
    @PreAuthorize("@ss.hasPermission('system:sms-template:delete')")
    public CommonResult<Boolean> deleteSmsTemplateList(@RequestParam("ids") List<Long> ids) {
        smsTemplateService.deleteSmsTemplateList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get SMS template")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public CommonResult<SmsTemplateResponse> getSmsTemplate(@RequestParam("id") Long id) {
        SmsTemplateEntity template = smsTemplateService.getSmsTemplate(id);
        return success(BeanUtils.toBean(template, SmsTemplateResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get SMS template page")
    @PreAuthorize("@ss.hasPermission('system:sms-template:query')")
    public CommonResult<PageResult<SmsTemplateResponse>> getSmsTemplatePage(@Valid SmsTemplatePageRequest pageVO) {
        PageResult<SmsTemplateEntity> pageResult = smsTemplateService.getSmsTemplatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, SmsTemplateResponse.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "export SMS template Excel")
    @PreAuthorize("@ss.hasPermission('system:sms-template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSmsTemplateExcel(@Valid SmsTemplatePageRequest exportRequest,
                                       HttpServletResponse response) throws IOException {
        exportRequest.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SmsTemplateEntity> list = smsTemplateService.getSmsTemplatePage(exportRequest).getList();
        // 导出 Excel
        ExcelUtils.write(response, "短信模板.xls", "数据", SmsTemplateResponse.class,
                BeanUtils.toBean(list, SmsTemplateResponse.class));
    }

    @PostMapping("/send-sms")
    @Operation(summary = "Send SMS")
    @PreAuthorize("@ss.hasPermission('system:sms-template:send-sms')")
    public CommonResult<Long> sendSms(@Valid @RequestBody SmsTemplateSendRequest sendRequest) {
        return success(smsSendService.sendSingleSmsToAdmin(sendRequest.getMobile(), null,
                sendRequest.getTemplateCode(), sendRequest.getTemplateParams()));
    }

}

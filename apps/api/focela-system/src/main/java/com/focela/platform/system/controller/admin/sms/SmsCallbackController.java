package com.focela.platform.system.controller.admin.sms;

import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.utils.servlet.ServletUtils;
import com.focela.platform.framework.tenant.core.aop.TenantIgnore;
import com.focela.platform.system.config.sms.enums.SmsChannelEnum;
import com.focela.platform.system.service.sms.SmsSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - SMS callback")
@RestController
@RequestMapping("/system/sms/callback")
public class SmsCallbackController {

    @Resource
    private SmsSendService smsSendService;

    @PostMapping("/aliyun")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Aliyun SMS callback", description = "see https://help.aliyun.com/document_detail/120998.html docs")
    public CommonResult<Boolean> receiveAliyunSmsStatus(HttpServletRequest request) throws Throwable {
        String text = ServletUtils.getBody(request);
        smsSendService.receiveSmsStatus(SmsChannelEnum.ALIYUN.getCode(), text);
        return success(true);
    }

    @PostMapping("/tencent")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Tencent Cloud SMS callback", description = "see https://cloud.tencent.com/document/product/382/52077 docs")
    public CommonResult<Boolean> receiveTencentSmsStatus(HttpServletRequest request) throws Throwable {
        String text = ServletUtils.getBody(request);
        smsSendService.receiveSmsStatus(SmsChannelEnum.TENCENT.getCode(), text);
        return success(true);
    }


    @PostMapping("/huawei")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Huawei Cloud SMS callback", description = "see https://support.huaweicloud.com/api-msgsms/sms_05_0003.html docs")
    public CommonResult<Boolean> receiveHuaweiSmsStatus(@RequestBody String requestBody) throws Throwable {
        smsSendService.receiveSmsStatus(SmsChannelEnum.HUAWEI.getCode(), requestBody);
        return success(true);
    }

    @PostMapping("/qiniu")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "Qiniu SMS callback", description = "see https://developer.qiniu.com/sms/5910/message-push docs")
    public CommonResult<Boolean> receiveQiniuSmsStatus(@RequestBody String requestBody) throws Throwable {
        smsSendService.receiveSmsStatus(SmsChannelEnum.QINIU.getCode(), requestBody);
        return success(true);
    }

}
package com.focela.platform.module.system.controller.admin.notify;

import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplatePageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplateResponse;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplateSaveRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.template.NotifyTemplateSendRequest;
import com.focela.platform.module.system.repository.entity.notify.NotifyTemplateEntity;
import com.focela.platform.module.system.service.notify.NotifySendService;
import com.focela.platform.module.system.service.notify.NotifyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;

@Tag(name = "Admin - Notify template")
@RestController
@RequestMapping("/system/notify-template")
@Validated
public class NotifyTemplateController {

    @Resource
    private NotifyTemplateService notifyTemplateService;

    @Resource
    private NotifySendService notifySendService;

    @PostMapping("/create")
    @Operation(summary = "create notify template")
    @PreAuthorize("@ss.hasPermission('system:notify-template:create')")
    public CommonResult<Long> createNotifyTemplate(@Valid @RequestBody NotifyTemplateSaveRequest createRequest) {
        return success(notifyTemplateService.createNotifyTemplate(createRequest));
    }

    @PutMapping("/update")
    @Operation(summary = "update notify template")
    @PreAuthorize("@ss.hasPermission('system:notify-template:update')")
    public CommonResult<Boolean> updateNotifyTemplate(@Valid @RequestBody NotifyTemplateSaveRequest updateRequest) {
        notifyTemplateService.updateNotifyTemplate(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete notify template")
    @Parameter(name = "id", description = "ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:notify-template:delete')")
    public CommonResult<Boolean> deleteNotifyTemplate(@RequestParam("id") Long id) {
        notifyTemplateService.deleteNotifyTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete notify template")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:notify-template:delete')")
    public CommonResult<Boolean> deleteNotifyTemplateList(@RequestParam("ids") List<Long> ids) {
        notifyTemplateService.deleteNotifyTemplateList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "get notify template")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notify-template:query')")
    public CommonResult<NotifyTemplateResponse> getNotifyTemplate(@RequestParam("id") Long id) {
        NotifyTemplateEntity template = notifyTemplateService.getNotifyTemplate(id);
        return success(BeanUtils.toBean(template, NotifyTemplateResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get notify template page")
    @PreAuthorize("@ss.hasPermission('system:notify-template:query')")
    public CommonResult<PageResult<NotifyTemplateResponse>> getNotifyTemplatePage(@Valid NotifyTemplatePageRequest pageVO) {
        PageResult<NotifyTemplateEntity> pageResult = notifyTemplateService.getNotifyTemplatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, NotifyTemplateResponse.class));
    }

    @PostMapping("/send-notify")
    @Operation(summary = "send notify message")
    @PreAuthorize("@ss.hasPermission('system:notify-template:send-notify')")
    public CommonResult<Long> sendNotify(@Valid @RequestBody NotifyTemplateSendRequest sendRequest) {
        if (UserTypeEnum.MEMBER.getValue().equals(sendRequest.getUserType())) {
            return success(notifySendService.sendSingleNotifyToMember(sendRequest.getUserId(),
                    sendRequest.getTemplateCode(), sendRequest.getTemplateParams()));
        } else {
            return success(notifySendService.sendSingleNotifyToAdmin(sendRequest.getUserId(),
                    sendRequest.getTemplateCode(), sendRequest.getTemplateParams()));
        }
    }
}

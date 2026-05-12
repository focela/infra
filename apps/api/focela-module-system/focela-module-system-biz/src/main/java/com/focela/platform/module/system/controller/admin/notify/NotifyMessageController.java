package com.focela.platform.module.system.controller.admin.notify;

import com.focela.platform.framework.apilog.core.annotation.ApiAccessLog;
import com.focela.platform.framework.common.enums.UserTypeEnum;
import com.focela.platform.framework.common.model.CommonResult;
import com.focela.platform.framework.common.model.PageResult;
import com.focela.platform.framework.common.utils.object.BeanUtils;
import com.focela.platform.module.system.controller.admin.notify.dto.message.NotifyMessageMyPageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.message.NotifyMessagePageRequest;
import com.focela.platform.module.system.controller.admin.notify.dto.message.NotifyMessageResponse;
import com.focela.platform.module.system.repository.entity.notify.NotifyMessageEntity;
import com.focela.platform.module.system.service.notify.NotifyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

import static com.focela.platform.framework.common.model.CommonResult.success;
import static com.focela.platform.framework.security.core.utils.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "Admin - My notifications")
@RestController
@RequestMapping("/system/notify-message")
@Validated
public class NotifyMessageController {

    @Resource
    private NotifyMessageService notifyMessageService;

    // ========== 管理所有的站内信 ==========

    @GetMapping("/get")
    @Operation(summary = "get notify message")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notify-message:query')")
    public CommonResult<NotifyMessageResponse> getNotifyMessage(@RequestParam("id") Long id) {
        NotifyMessageEntity message = notifyMessageService.getNotifyMessage(id);
        return success(BeanUtils.toBean(message, NotifyMessageResponse.class));
    }

    @GetMapping("/page")
    @Operation(summary = "get notify message page")
    @PreAuthorize("@ss.hasPermission('system:notify-message:query')")
    public CommonResult<PageResult<NotifyMessageResponse>> getNotifyMessagePage(@Valid NotifyMessagePageRequest pageVO) {
        PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getNotifyMessagePage(pageVO);
        return success(BeanUtils.toBean(pageResult, NotifyMessageResponse.class));
    }

    // ========== 查看自己的站内信 ==========

    @GetMapping("/my-page")
    @Operation(summary = "get my notify message page")
    public CommonResult<PageResult<NotifyMessageResponse>> getMyMyNotifyMessagePage(@Valid NotifyMessageMyPageRequest pageVO) {
        PageResult<NotifyMessageEntity> pageResult = notifyMessageService.getMyMyNotifyMessagePage(pageVO,
                getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return success(BeanUtils.toBean(pageResult, NotifyMessageResponse.class));
    }

    @PutMapping("/update-read")
    @Operation(summary = "mark notify message as read")
    @Parameter(name = "ids", description = "ID list", required = true, example = "1024,2048")
    public CommonResult<Boolean> updateNotifyMessageRead(@RequestParam("ids") List<Long> ids) {
        notifyMessageService.updateNotifyMessageRead(ids, getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return success(Boolean.TRUE);
    }

    @PutMapping("/update-all-read")
    @Operation(summary = "mark all notify message as read")
    public CommonResult<Boolean> updateAllNotifyMessageRead() {
        notifyMessageService.updateAllNotifyMessageRead(getLoginUserId(), UserTypeEnum.ADMIN.getValue());
        return success(Boolean.TRUE);
    }

    @GetMapping("/get-unread-list")
    @Operation(summary = "get current user latest notify message list, default 10 item")
    @Parameter(name = "size", description = "10")
    public CommonResult<List<NotifyMessageResponse>> getUnreadNotifyMessageList(
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        List<NotifyMessageEntity> list = notifyMessageService.getUnreadNotifyMessageList(
                getLoginUserId(), UserTypeEnum.ADMIN.getValue(), size);
        return success(BeanUtils.toBean(list, NotifyMessageResponse.class));
    }

    @GetMapping("/get-unread-count")
    @Operation(summary = "get current user unread notify message count")
    @ApiAccessLog(enable = false) // 由于前端会不断轮询该接口，记录日志没有意义
    public CommonResult<Long> getUnreadNotifyMessageCount() {
        return success(notifyMessageService.getUnreadNotifyMessageCount(
                getLoginUserId(), UserTypeEnum.ADMIN.getValue()));
    }

}

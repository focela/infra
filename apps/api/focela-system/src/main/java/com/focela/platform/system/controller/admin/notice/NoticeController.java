package com.focela.platform.system.controller.admin.notice;

import cn.hutool.core.lang.Assert;
import com.focela.platform.common.enums.UserTypeEnum;
import com.focela.platform.common.model.CommonResult;
import com.focela.platform.common.model.PageResult;
import com.focela.platform.common.utils.object.BeanUtils;
import com.focela.platform.infra.api.websocket.WebSocketSenderApi;
import com.focela.platform.system.controller.admin.notice.dto.NoticePageRequest;
import com.focela.platform.system.controller.admin.notice.dto.NoticeResponse;
import com.focela.platform.system.controller.admin.notice.dto.NoticeSaveRequest;
import com.focela.platform.system.entity.notice.NoticeEntity;
import com.focela.platform.system.service.notice.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.focela.platform.common.model.CommonResult.success;

@Tag(name = "Admin - Notice")
@RestController
@RequestMapping("/system/notice")
@Validated
@RequiredArgsConstructor
public class NoticeController {

        private final NoticeService noticeService;

        private final WebSocketSenderApi webSocketSenderApi;

    @PostMapping("/create")
    @Operation(summary = "create notice")
    @PreAuthorize("@ss.hasPermission('system:notice:create')")
    public CommonResult<Long> createNotice(@Valid @RequestBody NoticeSaveRequest createRequest) {
        Long noticeId = noticeService.createNotice(createRequest);
        return success(noticeId);
    }

    @PutMapping("/update")
    @Operation(summary = "update notice")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody NoticeSaveRequest updateRequest) {
        noticeService.updateNotice(updateRequest);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "delete notice")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "batch delete notice")
    @Parameter(name = "ids", description = "ID list", required = true)
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNoticeList(@RequestParam("ids") List<Long> ids) {
        noticeService.deleteNoticeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "get notice list")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<PageResult<NoticeResponse>> getNoticePage(@Validated NoticePageRequest pageRequest) {
        PageResult<NoticeEntity> pageResult = noticeService.getNoticePage(pageRequest);
        return success(BeanUtils.toBean(pageResult, NoticeResponse.class));
    }

    @GetMapping("/get")
    @Operation(summary = "get notice")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<NoticeResponse> getNotice(@RequestParam("id") Long id) {
        NoticeEntity notice = noticeService.getNotice(id);
        return success(BeanUtils.toBean(notice, NoticeResponse.class));
    }

    @PostMapping("/push")
    @Operation(summary = "push notice", description = "only send to websocket connection online user")
    @Parameter(name = "id", description = "ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> push(@RequestParam("id") Long id) {
        NoticeEntity notice = noticeService.getNotice(id);
        Assert.notNull(notice, "notice must not be empty");
        // push to online users via websocket
        webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), "notice-push", notice);
        return success(true);
    }

}

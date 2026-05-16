package com.focela.platform.system.api.notify;

import com.focela.platform.system.api.notify.dto.NotifySendSingleToUserRpcRequest;
import com.focela.platform.system.service.notify.NotifySendService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * Notification message send API implementation class
 */
@Service
public class LocalNotifyMessageSendApi implements NotifyMessageSendApi {

    @Resource
    private NotifySendService notifySendService;

    @Override
    public Long sendSingleMessageToAdmin(NotifySendSingleToUserRpcRequest request) {
        return notifySendService.sendSingleNotifyToAdmin(request.getUserId(),
                request.getTemplateCode(), request.getTemplateParams());
    }

    @Override
    public Long sendSingleMessageToMember(NotifySendSingleToUserRpcRequest request) {
        return notifySendService.sendSingleNotifyToMember(request.getUserId(),
                request.getTemplateCode(), request.getTemplateParams());
    }

}

package com.focela.platform.module.system.api.notify;

import com.focela.platform.module.system.api.notify.dto.NotifySendSingleToUserRpcRequest;
import com.focela.platform.module.system.service.notify.NotifySendService;
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
    public Long sendSingleMessageToAdmin(NotifySendSingleToUserRpcRequest reqDTO) {
        return notifySendService.sendSingleNotifyToAdmin(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams());
    }

    @Override
    public Long sendSingleMessageToMember(NotifySendSingleToUserRpcRequest reqDTO) {
        return notifySendService.sendSingleNotifyToMember(reqDTO.getUserId(),
                reqDTO.getTemplateCode(), reqDTO.getTemplateParams());
    }

}

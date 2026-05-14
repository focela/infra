package com.focela.platform.module.system.api.notify;

import com.focela.platform.module.system.api.notify.dto.NotifySendSingleToUserRpcRequest;

import jakarta.validation.Valid;

/**
 * Notification message send API interface
 */
public interface NotifyMessageSendApi {

    /**
     * Send a single notification message to an Admin user
     *
     * @param reqDTO send request
     * @return sent message ID
     */
    Long sendSingleMessageToAdmin(@Valid NotifySendSingleToUserRpcRequest reqDTO);

    /**
     * Send a single notification message to a Member user
     *
     * @param reqDTO send request
     * @return sent message ID
     */
    Long sendSingleMessageToMember(@Valid NotifySendSingleToUserRpcRequest reqDTO);

}
